package io.forge

import java.nio.file.Path

import data.Data
import reo.HSArgs

object Protocol {
  case class Job(forge: Path, target: Path, hSArgs: HSArgs)
  case class Result(data: Data)
}
