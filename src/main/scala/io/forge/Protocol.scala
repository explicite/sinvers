package io.forge

import java.nio.file.Path

import io.DONArgs
import reo.HSArgs

object Protocol {

  case class Job(forge: Path, target: Path, args: DONArgs)

  case class Result(time: List[Double], load: List[Double], height: List[Double], velocity: List[Double])

  case class Restart(job: Job)

  case object Stop

}
