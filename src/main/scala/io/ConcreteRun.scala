package io

import data.DataFile
import opt.InversFunction

object ConcreteRun {

  def main(args: Array[String]): Unit = {

    val fx2Dir = "C:\\Users\\Jan\\Desktop\\Forge2-V3.0"
    val workingDirectory = "C:\\Users\\Jan\\Desktop\\sym"
    val process = Forge(fx2Dir)

    val experimentDirectory = "C:\\Users\\Jan\\Desktop\\mgr\\HA000490.D01"
    val don = DON(new java.io.File(s"$workingDirectory\\newSym.don"))
    val experimentData = DataFile(new java.io.File(experimentDirectory))
    val function = InversFunction(process, don, experimentData)

    val args3 = Seq(
      16974.099461971975,
      -6.234595507956861E-4,
      -0.9560788229496123,
      -0.030825971878518845,
      -0.2510517267681572,
      -2.791604866537811E-5,
      0.0,
      -1.5760965741618997E-4,
      1.0331795225855046E-4,
      0.2712698745964402,
      0.0
    )
    function.fitness(args3)
  }

}
