import scalariform.formatter.preferences._

name := "sinvers"

version := "0.1"

scalaVersion := "2.11.6"

resolvers += "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"

// ScalaFX
unmanagedJars in Compile += Attributed.blank(
  file(System.getenv("JAVAFX_HOME") + "/rt/lib/jfxrt.jar"))

libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "8.0.31-R7",
  "com.typesafe.akka" %% "akka-actor" % "2.3.9",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.9",
  "org.slf4j" % "slf4j-simple" % "1.7.10",
  "org.scalacheck" %% "scalacheck" % "1.12.2" % "test",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "com.storm-enroute" %% "scalameter" % "0.6" % "test"
)

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

logBuffered := false

parallelExecution in Test := false

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(PreserveDanglingCloseParenthesis, true)
