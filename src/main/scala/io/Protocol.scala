package io

import java.nio.file.Path

import data.DataContainer

object Protocol {
  case class Optimize(forge: Path, mesh: Path, out: Path, experiment: DataContainer, temperature: Double, strainRate: Double)
}
