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
      4699.285919249167,
      -0.0011415601892736061,
      -0.31645294641921773,
      0.0396536972818135,
      -0.05968658294035687,
      1.7824317209455084E-5,
      0.0,
      7.322474544897119E-5,
      4.933482677015285E-5,
      0.5247387458805934,
      0.0
    )
    function.fitness(args3)
  }

}
