name := "Visualize"

version := "1.0"

lazy val `visualize` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq( cache , ws   , specs2 % Test,
                            "mysql" % "mysql-connector-java" % "6.0.5",
                            "com.typesafe.play" %% "play-slick" % "2.0.2",
                            "com.typesafe.play" %% "play-slick-evolutions" % "2.0.2",
                            "com.github.tototoshi" %% "slick-joda-mapper" % "2.2.0",
                            "joda-time" % "joda-time" % "2.7",
                            "org.joda" % "joda-convert" % "1.7",
                            "com.mohiva" %% "play-silhouette" % "4.0.0",
                            "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0",
                            "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0",
                            "com.mohiva" %% "play-silhouette-persistence" % "4.0.0",
                            "com.mohiva" %% "play-silhouette-testkit" % "4.0.0" % "test",
                            "com.adrianhurt" %% "play-bootstrap" % "1.1-P25-B4",
                            "com.iheart" %% "ficus" % "1.4.0",
                            "net.codingwell" %% "scala-guice" % "4.1.0",
                            filters )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"  