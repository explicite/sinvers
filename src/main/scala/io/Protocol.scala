package io

import java.nio.file.Path

import data.{ DataContainer, Samples }
import opt.StaticInterval
import ui.view.InversView

object Protocol {

  case class OptimizeInvers(forge: Path,
    sample: Samples,
    experiment: DataContainer,
    temperature: Double,
    strainRate: Double,
    bounds: Seq[StaticInterval],
    wolfs: Int,
    iterations: Int)

  case class OptimizeFullInvers(args: Seq[InversView],
    bounds: Seq[StaticInterval],
    wolfs: Int,
    iterations: Int)

}
