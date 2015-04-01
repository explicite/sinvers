package io

import java.nio.file.Path

object Protocol {
  case class Optimize(forge: Path, mesh: Path, out: Path, experiment: Path, temperature: Double)
}
