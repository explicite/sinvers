package io

import java.nio.file.Path

import data.{ DataContainer, Samples }
import opt.StaticInterval

object Protocol {

  case class Optimize(forge: Path,
    sample: Samples,
    experiment: DataContainer,
    temperature: Double,
    strainRate: Double,
    bounds: Seq[StaticInterval],
    wolfs: Int,
    iterations: Int)

}
