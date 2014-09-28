import AssemblyKeys._

import AssemblyKeys._ // put this at the top of the file

assemblySettings

name := "sinvers"

test in assembly := {}

mainClass in assembly := Some("io.Application")

mergeStrategy in assembly := { case _ => MergeStrategy.last }
