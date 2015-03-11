name := "sinvers"

version := "0.1"

scalaVersion := "2.11.6"

resolvers += "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"

libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "8.0.31-R7",
  "com.typesafe.akka" %% "akka-actor" % "2.3.9",
  "org.scalacheck" %% "scalacheck" % "1.12.2" % "test",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "com.storm-enroute" %% "scalameter" % "0.6" % "test"
)

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

logBuffered := false

parallelExecution in Test := false

scalariformSettings
