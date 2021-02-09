import org.scalajs.jsdependencies.sbtplugin.JSDependenciesPlugin.autoImport._
import play.sbt.PlayImport._
import sbt._

/**
  * Application settings. Configure the build for your application here.
  * You normally don't have to touch the actual build definition after this.
  */
object Settings {
  /** The application name */
  val name = """play-cms Demo"""

  /** The application version */
  val version = """1.1"""

  /** Creating organization **/
  val organization = """ch.insign"""

  /** Options for the scala compiler */
  val scalacOptions = Seq(
    "-Xlint",
    "-unchecked",
    "-deprecation",
    "-feature"
  )

  /** Declare global dependency versions here to avoid mismatches in multi part dependencies */
  object versions {
    val scala = "2.12.8"

    // CMS version
    val playCms = "2.7.+"

    // JVM libraries
    val mysqlConnector = "5.1.38"
    val eclipseLink = "2.7.2"
    val apacheCommons = "3.2.1"
    val vavr = "0.9.2"
    val mapStruct = "1.1.0.Final"
    val playBootstrap = "1.4-P26-B4"
    val mokito = "2.8.47"
    val h2 = "1.4.192"

    // JS libraries
    val scalajsTools = "0.6.22"
    val autowire = "0.2.6"
    val booPickle = "1.1.2"
    val uTest = "0.3.1"
    val jQueryFacade = "1.0-RC3"
    val log4js = "1.4.10"
    val flexSlider = "2.2.2"
    val jqueryForm = "3.51"
    val jqueryUI = "1.10.3"
    val jQuery = "3.3.1"
    val bootstrap = "4.2.1"
    val playScripts = "1.1.1"
    val fontAwesome = "4.7.0"
  }

  /** Dependencies only used by the JVM project */
  val jvmDependencies = Def.setting(Seq(
    javaCore,
    javaJpa,
    guice,
    "com.vmunier" %% "scalajs-scripts" % versions.playScripts,
    "org.webjars" % "font-awesome" % versions.fontAwesome % Provided,
    "org.webjars.bower" % "bootstrap" % versions.bootstrap % Provided,
    "mysql" % "mysql-connector-java" % versions.mysqlConnector,
    "org.eclipse.persistence" % "eclipselink" % versions.eclipseLink % Provided,
    "org.apache.commons" % "commons-lang3" % versions.apacheCommons % Provided,
    "io.vavr" % "vavr" % versions.vavr % Provided,
    "org.mapstruct" % "mapstruct-jdk8" % versions.mapStruct,
    "org.mapstruct" % "mapstruct-processor" % versions.mapStruct,
    "com.adrianhurt" %% "play-bootstrap" % versions.playBootstrap,
    "org.mockito" % "mockito-core" % versions.mokito % Test,
    "com.h2database" % "h2" % versions.h2 % Test
  ))

  /** CMS Dependencies used by the JVM project */
  val playCmsVersion: String = devmodVersion("playcms.version", versions.playCms)
  val playCmsLocal: Boolean = devmodIsLocal("playcms.version")

  /** Environment: dockerized or simple*/
  val withDocker: Boolean = isRunWithDocker("environment")

  /** Dependencies for external JS libs that are bundled into a single .js file according to dependency order */
  val jsDependencies = Def.setting(Seq(
    "org.webjars" % "jquery" % versions.jQuery / "jquery.js" minified "jquery.min.js",
    "org.webjars" % "bootstrap" % versions.bootstrap / "bootstrap.bundle.js" minified "bootstrap.bundle.min.js" dependsOn "jquery.js",
    "org.webjars" % "log4javascript" % versions.log4js / "js/log4javascript_uncompressed.js" minified "js/log4javascript.js",
    "org.webjars" % "FlexSlider" % versions.flexSlider / "jquery.flexslider.js" minified "jquery.flexslider-min.js",
    "org.webjars" % "jquery-form" % versions.jqueryForm / "jquery.form.js",
    "org.webjars" % "jquery-ui" % versions.jqueryUI / "ui/jquery-ui.js" minified "ui/minified/jquery-ui.min.js"
  ))

  val resolvers = Def.setting(Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    Resolver.bintrayRepo("insign", "play-cms"),
    Resolver.jcenterRepo
  ))

  /* Helpers */

  private def devmodIsLocal(prop: String): Boolean = {
    sys.props.get(prop).exists(_ == "local")
  }

  private def devmodVersion(prop: String, default: String): String = {
    sys.props.get(prop).filter(_ != "local").getOrElse(default)
  }

  private def isRunWithDocker(prop: String): Boolean = {
    sys.props.get(prop).exists(_ == "docker")
  }

}
