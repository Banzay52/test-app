import sbt.Keys._
import sbt._

/**
  * Imports MySQL database using settings from env vars.
  *
  * Example usage:
  * `
  *   MYSQLDUMP_HOST=remotehost \
  *   MYSQLDUMP_USER=user \
  *   MYSQLDUMP_PASSWORD=pwd \
  *   MYSQLDUMP_DATABASES=db1 db2 db3 \
  *   MYSQL_HOST=localhost \
  *   MYSQL_ROOT_PASSWORD=rootpwd \
  *     sbt mysqlImport
  * `
  */
object MySQL {

  lazy val mysqlDumpFile: SettingKey[File] = settingKey[File]("mysql-dump-file")
  lazy val mysqlDownloadDump: TaskKey[File] = taskKey[File]("mysql-download-dump")
  lazy val mysqlImportDump: TaskKey[Unit] = taskKey[Unit]("mysql-import-dump")
  lazy val mysqlDownloadAndImportDump: TaskKey[Unit] = taskKey[Unit]("mysql-download-and-import-dump")

  val defaultSettings: Seq[Setting[_]] = Seq(
    mysqlDumpFile := baseDirectory.value / "devops" / "docker" / "db" / "dump.sql",

    mysqlDownloadDump := {
      import sys.process._

      val dump = mysqlDumpFile.value

      println(s"Exporting remote database into ${dump.getName}...")
      mysqldump #> dump !

      dump
    },

    mysqlImportDump := {
      import sys.process._

      val dump = mysqlDumpFile.value

      if (!dump.exists()) {
        println(s"Exporting remote database into ${dump.getName}...")
        mysqldump #> dump !
      }

      println(s"Importing local database from ${dump.getName}...")
      mysqlimport #< dump !
    },

    mysqlDownloadAndImportDump := {
      import sys.process._

      val dump = mysqlDumpFile.value

      println(s"Exporting remote database into ${dump.getName}...")
      mysqldump #> dump !

      println(s"Importing local database from ${dump.getName}...")
      mysqlimport #< dump !
    }
  )

  private def mysqldump = "mysqldump " + params(
    "--host" -> sys.env.get("MYSQLDUMP_HOST"),
    "--port" -> sys.env.get("MYSQLDUMP_PORT"),
    "--user" -> sys.env.get("MYSQLDUMP_USER"),
    "--password" -> sys.env.get("MYSQLDUMP_PASSWORD"),
    "--routines" -> true,
    "--triggers" -> true,
    "--databases" -> sys.env.get("MYSQLDUMP_DATABASES").map(_.split(" ").toList),
    "--single-transaction" -> true,
    "--add-drop-database" -> true,
    "--compress" -> true
  )

  private def mysqlimport = "mysql " + params(
    "--batch" -> true,
    "--host" -> sys.env.get("MYSQL_HOST"),
    "--port" -> sys.env.get("MYSQL_PORT"),
    "--user" -> sys.env.get("MYSQL_ROOT_PASSWORD").map(_ => "root").orElse(sys.env.get("MYSQL_USER")),
    "--password" -> sys.env.get("MYSQL_ROOT_PASSWORD").orElse(sys.env.get("MYSQL_PASSWORD"))
  )

  private def params(params: (String, Any)*) = {
    val param: PartialFunction[(String, Any), String] = {
      case (name, Some(value: String)) if name.startsWith("--") => s"$name=$value"
      case (name, Some(value: String)) if name.startsWith("-") => s"$name $value"
      case (name, Some(values: List[_])) => s"$name ${values.mkString(" ")}"
      case (name, true) => name
    }

    params.filter(param.isDefinedAt).map(param.apply).mkString(" ")
  }
}
