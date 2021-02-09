import play.sbt.PlayImport.PlayKeys.devSettings

lazy val uploadsFolder: String =
  (file(".") / "data" / "uploads").getAbsoluteFile.getAbsolutePath

lazy val mysqlDataFolder: String =
  (file(".") / "data" / "mysql").getAbsoluteFile.getAbsolutePath

lazy val mailhogDataFolder: String =
  (file(".") / "data" / "mailhog").getAbsoluteFile.getAbsolutePath

scalaVersion := Settings.versions.scala

lazy val root = project.in(file("."))
  .settings(
    name := Settings.name,
    organization := Settings.organization,
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    libraryDependencies ++= Settings.jvmDependencies.value,
    pipelineStages := Seq(uglify, digest, gzip),

    resolvers ++= Settings.resolvers.value,

    // a simple test support of application
    javaOptions in Test += "-Dconfig.file=conf/application.test.conf",

    // Fixes problem with loading JPA entities in prod mode
    // see: https://github.com/playframework/playframework/issues/4590
    PlayKeys.externalizeResources := false,

    // Root Path for included CSS in main.less
    SassKeys.assetRootURL := "/",
    // Adjust Urls imported in main.less
    // LessKeys.relativeUrls := true,
    // compress CSS
    // LessKeys.compress in Assets := true,

    // Allow ScalaDoc compilation for annotated classes
    // see https://github.com/scala/bug/issues/11365
    scalacOptions in (Compile, doc) += "-no-java-comments",

    maintainer in Docker := "insign gmbh <info@insign.ch>",
    packageName in Docker := "demo",
    version in Docker := System.getProperty("docker.version", Settings.version),
    packageSummary in Docker := "The play-cms demo application.",
    aggregate in Docker := false,
    dockerUsername := Some(System.getProperty("docker.user", "play-cms")),
    dockerRepository := Some(System.getProperty("docker.registry", "docker.insign.rocks")),

    // Add java options for packager
    javaOptions in Universal ++= Seq(
      // -J params will be added as jvm parameters
      "-J-Xmx2g",
      "-J-Xms256m",
      "-Dpidfile.path=/dev/null"
    )
  )
  .settings(if (Settings.withDocker) Seq(
    devSettings := {
      val envVars = Map(
        "APPLICATION_HOST"        -> "localhost",
        "MYSQL_HOST"              -> "localhost",
        "MYSQL_PORT"              -> "3306",
        "MYSQL_DATABASE"          -> "db_play-cms-demo",
        "MYSQL_USER"              -> "play-cms-demo",
        "MYSQL_PASSWORD"          -> "s3cr3t",
        "ELASTICSEARCH_ENABLE"    -> "false",
        "ELASTICSEARCH_INDEX"     -> "play-cms-demo",
        "ELASTICSEARCH_HOST"      -> "localhost",
        "ELASTICSEARCH_PORT"      -> "9200",
        "SMTP_MOCK"               -> "false",
        "SMTP_HOST"               -> "localhost",
        "SMTP_PORT"               -> "1025",
        "FILEMANAGER_BASE_URL"    -> "http://localhost:8035/"
      )
      envVars.foreach { case (k, v) =>
        java.lang.System.setProperty(k, v)
      }
      envVars.toSeq
    },
    dockerContainers := Seq(
      /*DockerContainer(
        id = "elasticsearch",
        name = "docker.elastic.co/elasticsearch/elasticsearch",
        version = "6.4.0",
        ports = Seq(
          9200 `:` 9200,
          9300 `:` 9300
        ),
        environment = Map(
          "discovery.type" -> "single-node"
        )
      ),*/
      DockerContainer(
        id = "play-cms-demo_mysql",
        name = "mysql",
        version = "5.7",
        ports = Seq(
          3306 `:` 3306
        ),
        volumes = Seq(
          "/var/lib/mysql" `:` mysqlDataFolder
        ),
        environment = Map(
          "MYSQL_ROOT_PASSWORD" -> "s3cr3t",
          "MYSQL_DATABASE"      -> "db_play-cms-demo",
          "MYSQL_USER"          -> "play-cms-demo",
          "MYSQL_PASSWORD"      -> "s3cr3t"
        )
      ),
      DockerContainer(
        id = "play-cms-demo_mailhog",
        name = "mailhog/mailhog",
        version = "v1.0.0",
        ports = Seq(
          1025 `:` 1025,
          8025 `:` 8025
        ),
        volumes = Seq(
          "/home/mailhog" `:` mailhogDataFolder
        ),
        environment = Map(
          "MH_CORS_ORIGIN"  -> "*",
          "MH_STORAGE"      -> "maildir",
          "MH_MAILDIR_PATH" -> "/home/mailhog"
        )
      ),
      DockerContainer(
        id = "play-cms-demo_filemanager",
        name = "insign/responsive-filemanager",
        ports = Seq(
          80 `:` 8035
        ),
        volumes = Seq(
          "/var/www/html/source" `:` s"$uploadsFolder/source",
          "/var/www/html/thumbs" `:` s"$uploadsFolder/thumbs"
        ),
        environment = Map(
          "AUTH_BASE_URL" -> "http://app:9000", // This works because `app` is in the container's /etc/hosts
          "AUTH_VALIDATE_SSL" -> "false"
        )
      )
    )
  ) else Seq(
    devSettings := {
      val envVars = Map(
        "APPLICATION_HOST"        -> "localhost",
        "MYSQL_DATABASE"          -> "db_play-cms-demo",
        "MYSQL_USER"              -> "play-cms-demo",
        "MYSQL_PASSWORD"          -> "s3cr3t"
      )
      envVars.foreach { case (k, v) =>
        java.lang.System.setProperty(k, v)
      }
      envVars.toSeq
    }
  ))
  .enablePlugins(PlayJava, SbtWeb, DockerPlugin, DockerRun)
  .devModules(
    "ch.insign" %% "play-cms" % Settings.playCmsVersion as "cms" at "../play-cms" when Settings.playCmsLocal,
    "ch.insign" %% "play-auth" % Settings.playCmsVersion as "auth" at "../play-cms" when Settings.playCmsLocal,
    "ch.insign" %% "play-commons" % Settings.playCmsVersion as "commons" at "../play-cms" when Settings.playCmsLocal,
    "ch.insign" %% "play-cache-ehcache" % Settings.playCmsVersion as "cacheEhcache" at "../play-cms" when Settings.playCmsLocal,
    "ch.insign" %% "play-theme-metronic" % Settings.playCmsVersion as "metronic" at "../play-cms" when Settings.playCmsLocal)
