import play.api.libs.json._
import sbt._
import sbt.Keys._

import scala.util.Try
import scala.sys.process._

final class PortOps(local: Int) {

  def `:`(container: Int): PortMapping =
    PortMapping(local, container)

}

final class VolumeOps(local: String) {

  def `:`(container: String): VolumeMapping =
    VolumeMapping(Some(local), container)

}

final case class PortMapping(local: Int, container: Int)

object PortMapping {

  def apply(port: Int): PortMapping =
    PortMapping(port, port)

}

final case class VolumeMapping(local: Option[String], container: String)

object VolumeMapping {

  def apply(volume: String): VolumeMapping =
    VolumeMapping(None, volume)

}

final case class DockerContainer(id: String,
                                 name: String,
                                 version: String = "latest",
                                 ports: Seq[PortMapping] = Seq(),
                                 volumes: Seq[VolumeMapping] = Seq(),
                                 environment: Map[String, String] = Map())

class JsValueOps(val jsValue: JsValue) {

  def asArray: Array[JsValue] =
    jsValue.as[JsArray].value.toArray

  def asString: String =
    jsValue.as[JsString].value

  def field(field: String): JsValue =
    jsValue.as[JsObject].value(field)

}

object DockerRun extends AutoPlugin {

  object autoImport {

    implicit def toPortOps(port: Int): PortOps =
      new PortOps(port)

    implicit def toPortMapping(port: Int): PortMapping =
      PortMapping(port, port)

    implicit def toVolumeOps(volume: String): VolumeOps =
      new VolumeOps(volume)

    implicit def toVolumeMapping(volume: String): VolumeMapping =
      VolumeMapping(volume)

    lazy val dockerPublicHost: SettingKey[String] =
      settingKey("A public host in the internet, to get the ip by using ip route")

    lazy val dockerDevice: SettingKey[String] =
      settingKey("Device through which to connect to the docker host")

    lazy val dockerContainers: SettingKey[Seq[DockerContainer]] =
      settingKey("Docker containers to run before the app starts")

    lazy val dockerBin: TaskKey[String] =
      taskKey("Obtains the docker command")

    lazy val dockerHostIp: TaskKey[String] =
      taskKey("Obtains the docker host's ip")

    lazy val dockerRun: TaskKey[Seq[DockerContainer]] =
      taskKey("Runs the docker containers")

  }

  import autoImport._

  override def projectSettings: Seq[Setting[_]] = Seq(
    run := {
      dockerRun.value
      (run in Compile).evaluated
    },
    dockerPublicHost := "8.8.8.8",
    dockerDevice := "docker0",
    dockerBin := { { "which docker" !! }.split("\n").head },
    dockerHostIp := { obtainDockerHostIp(dockerPublicHost.value, dockerDevice.value) },
    dockerRun := { runDocker(dockerBin.value, dockerHostIp.value, dockerContainers.value) },
    dockerContainers := Nil
  )

  private def runDocker(dockerBin: String, dockerHostIp: String, dockerContainers: Seq[DockerContainer]): Seq[DockerContainer] =
    dockerContainers.map(runDockerContainer(dockerBin, dockerHostIp))

  private def runDockerContainer(dockerBin: String, dockerHostIp: String)(container: DockerContainer): DockerContainer = {
    if(!dockerContainerIsRunning(dockerBin, container)) {
      startDockerContainer(dockerBin, dockerHostIp, container)
    }
    container
  }

  private def obtainDockerHostIp(publicHost: String, dockerDevice: String): String =
    obtainDockerHostIpByRoute(publicHost)
      .orElse(obtainDockerHostIpByDevice(dockerDevice))
      .getOrElse(throw new RuntimeException(s"Could not find docker host ip for device $dockerDevice"))

  private def obtainDockerHostIpByRoute(publicHost: String): Option[String] =
    for {
      ipLine <- Try({ s"ip route get $publicHost" !! }.split("\n").head).toOption
      ip     <- ipLine.split(" ").toList.filterNot(_.isEmpty).drop(6).headOption
    } yield ip

  private def obtainDockerHostIpByDevice(dockerDevice: String): Option[String] =
    for {
      ipLine <- { s"ip addr show $dockerDevice" !! }.split("\n").toList.find(_.startsWith("    inet "))
      subnet <- ipLine.split(" ").toList.drop(5).headOption
      ip     <- subnet.split("/").toList.headOption
    } yield ip

  private def dockerContainerIsRunning(dockerBin: String, container: DockerContainer): Boolean = {
    val dockerPsCmd = s"""$dockerBin ps -a""" // not working: --format "{{.ID}} {{.Names}}"
    val dockerPs: String = { dockerPsCmd !! }
    val containerLines = dockerPs.split("\n").tail
    containerLines.exists { line =>
      val infos = line.split(" ")
      val containerId = infos.head
      val containerName = infos.last
      if (container.id == containerName)
        if (isContainerUpToDate(dockerBin, containerId, container)) {
          println(s"Docker container $containerName is up-to-date.")
          true
        } else {
          removeDockerContainer(dockerBin, containerId)
          false
        }
      else
        false
    }
  }

  private implicit def toJsValueOps(jsValue: JsValue): JsValueOps =
    new JsValueOps(jsValue)

  private def isContainerUpToDate(dockerBin: String, containerId: String, container: DockerContainer): Boolean = {
    val json = { s"""$dockerBin inspect $containerId""" !! }
    val jsonContainer = Json.parse(json).asArray.head
    isUp(jsonContainer) &&
      compareImage(jsonContainer, container.name, container.version) &&
      comparePorts(jsonContainer, container.ports) &&
      compareVolumes(jsonContainer, container.volumes) &&
      compareEnvVars(jsonContainer, container.environment)
  }

  private def isUp(value: JsValue): Boolean =
    value
      .field("State")
      .field("Status")
      .asString == "running"

  private def compareImage(jsObject: JsValue, image: String, version: String): Boolean =
    jsObject
      .field("Config")
      .field("Image")
      .asString == s"$image:$version"

  private def comparePorts(jsObject: JsValue, ports: Seq[PortMapping]): Boolean =
    ports.forall { portMapping =>
      jsObject
        .field("HostConfig")
        .field("PortBindings")
        .field(s"${portMapping.local}/tcp")
        .asArray.exists { portBinding =>
        portBinding
          .field("HostPort")
          .asString == s"${portMapping.container}"
      }
    }

  private def compareVolumes(jsObject: JsValue, volumes: Seq[VolumeMapping]): Boolean =
    volumes.forall { volumeMapping =>
      jsObject
        .field("Mounts")
        .asArray.exists { mount =>
        def matchSource = volumeMapping.local match {
          case None => mount.field("Type").asString == "volume"
          case Some(x) => mount.field("Type").asString == "bind" && mount.field("Source").asString == x
        }
        matchSource && mount.field("Destination").asString == volumeMapping.container
      }
    }

  private def compareEnvVars(jsObject: JsValue, environment: Map[String, String]): Boolean =
    environment.toSeq.forall { case (k, v) =>
      jsObject
        .field("Config")
        .field("Env")
        .asArray
        .map(_.asString)
        .contains(s"$k=$v")
    }

  private def removeDockerContainer(dockerBin: String, containerId: String): Unit = {
    { s"""$dockerBin rm -f $containerId""" !! }
    println(s"Removed: $containerId")
  }

  private def startDockerContainer(dockerBin: String, dockerHostIp: String, container: DockerContainer): Unit = {
    println(s"Starting ${container.name}:${container.version} as ${container.id}")
    val containerPorts = container.ports.map(port => s"-p ${port.local}:${port.container}").mkString(" ")
    val containerVolumes = container.volumes.map(volume => s"-v ${volume.local.map(_ + ":").getOrElse("")}${volume.container}").mkString(" ")
    val containerEnv = container.environment.toSeq.map{ case (k: String, v: String) => s"-e $k=$v" }.mkString(" ")
    val dockerRunCommand = s"""$dockerBin run --add-host app:$dockerHostIp --name ${container.id} -d $containerPorts $containerVolumes $containerEnv ${container.name}:${container.version}"""
    val containerId: String = { dockerRunCommand !! }
    println(s"Started: $containerId")
  }

}
