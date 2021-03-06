import scalariform.formatter.preferences._

name := "sinvers"

version := "0.1"

scalaVersion := "2.11.7"

resolvers += "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"

libraryDependencies ++= Seq(
  "org.hsqldb" % "hsqldb" % "2.3.2",
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "org.scalafx" %% "scalafx" % "8.0.40-R8",
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.11",
  "com.typesafe.akka" % "akka-stream-experimental_2.11" % "1.0-RC3",
  "org.slf4j" % "slf4j-simple" % "1.7.10",
  "org.scalacheck" %% "scalacheck" % "1.12.2" % "test",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "com.storm-enroute" %% "scalameter" % "0.6" % "test"
)

fork := true

fork in run := true

fork in test := true

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

logBuffered := false

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(PreserveDanglingCloseParenthesis, true)
