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
      607.0044998611718,
      -0.006566258245714811,
      -0.21007539044798862,
      -0.2076373049421277,
      -0.046202182847251284,
      1.3470572457687486E-4,
      0.0,
      -0.15230128443911398,
      1.9711823426304953E-4,
      0.6570079207003687,
      0.0
    )
    function.fitness(args3)
  }

}
