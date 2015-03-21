package io.forge

import java.nio.file.Path

import reo.HSArgs

object Protocol {

  case class Job(forge: Path, parameters: Parameters)

  case class Result(time: List[Double], load: List[Double], height: List[Double], velocity: List[Double])

  case class Restart(job: Job)

  case object Stop

  //data container
  case class Parameters(mesh: Path, out: Path, steering: Path, temperature: Double, hs: HSArgs)

}
