package io.forge

import java.nio.file.Path

import reo.HSArgs

object Protocol {

  case class Job(forge: Path, target: Path, hSArgs: HSArgs)

  case class Result(time: List[Double], load: List[Double], height: List[Double], velocity: List[Double])

  case class Restart(job: Job)

}
