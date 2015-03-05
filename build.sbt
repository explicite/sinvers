name := "sinvers"

version := "0.1"

scalaVersion := "2.11.5"

resolvers += "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"

libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.11.5" % "test",
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
  "com.github.wookietreiber" % "scala-chart_2.11" % "0.4.2",
  "com.lowagie" % "itext" % "4.2.1",
  "com.storm-enroute" %% "scalameter" % "0.6" % "test"
)

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

logBuffered := false

parallelExecution in Test := false

scalariformSettings
