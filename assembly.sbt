import AssemblyKeys._ 

assemblySettings

name := "sinvers"

test in assembly := {}

mainClass in assembly := Some("io.Application")

mergeStrategy in assembly := { case _ => MergeStrategy.last }
