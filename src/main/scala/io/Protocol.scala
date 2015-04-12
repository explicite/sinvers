package io

import java.nio.file.Path

import data.{ Samples, DataContainer }

object Protocol {
  case class Optimize(forge: Path, sample: Samples, experiment: DataContainer, temperature: Double, strainRate: Double)
}
