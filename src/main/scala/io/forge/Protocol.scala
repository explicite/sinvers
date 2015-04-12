package io.forge

import java.nio.file.Path

import data.Samples
import opt.Interval
import reo.HSArgs

object Protocol {

  case class Job(forge: Path, parameters: Parameters)

  case class Restart(job: Job)

  case object Stop

  //data container
  case class Parameters(sample: Samples, steering: Path, interval: Interval, temperature: Double, hs: HSArgs)

}
