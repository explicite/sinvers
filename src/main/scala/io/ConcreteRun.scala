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
      8391.038944327514,
      -0.0029205021897638073,
      -0.2474891475591282,
      0.19531189181363873,
      -0.04314211922812594,
      1.0225047101330986E-4,
      0.0,
      -0.15094992716859068,
      1.4944200052458E-4,
      -0.7877795815755233,
      0.0
    )
    function.fitness(args3)
  }

}
