resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/maven-releases/"
resolvers += Resolver.bintrayRepo("insign", "play-cms")
resolvers += Resolver.bintrayIvyRepo("insign", "sbt-plugins")

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.7.3"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.7.3")
addSbtPlugin("com.typesafe.sbt" % "sbt-play-enhancer" % "1.2.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.24")
addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.4")
addSbtPlugin("com.typesafe.sbt" % "sbt-uglify" % "2.0.0")
addSbtPlugin("org.irundaia.sbt" % "sbt-sassify" % "1.4.13")
addSbtPlugin("org.scala-js" % "sbt-jsdependencies" % "1.0.0-M8")
addSbtPlugin("ch.insign" %% "sbt-dev-module" % "1.0.6")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.11")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.0")
