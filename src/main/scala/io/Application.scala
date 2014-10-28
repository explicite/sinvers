package io

import data.DataFile
import opt.{GreyWolfOptimizer, IntervalWithTolerance, InversFunction, StaticInterval}


object Application {

  def main(args: Array[String]) = {
    val fx2Dir = "C:\\Users\\Jan\\Desktop\\Forge2-V3.0"
    val workingDirectory = "C:\\Users\\Jan\\Desktop\\sym"
    val process = Forge(fx2Dir)

    val experimentDirectory = "C:\\Users\\Jan\\Desktop\\mgr\\HA000490.D01"
    val don = DON(new java.io.File(s"$workingDirectory\\newSym.don"))
    val experimentData = DataFile(new java.io.File(experimentDirectory))
    val function = InversFunction(process, don, experimentData)
    val bounds = Seq(
      IntervalWithTolerance(607.0044998611718, 0.1), //a1 ok
      IntervalWithTolerance(-0.006566258245714811, 1), //m1 ok
      IntervalWithTolerance(-0.21007539044798862, 1), //m2 ok
      IntervalWithTolerance(-0.2076373049421277, 1), //m3 ok
      IntervalWithTolerance(-0.046202182847251284, 1), //m4 ok
      IntervalWithTolerance(1.3470572457687486E-4, 1), //m5
      StaticInterval(0.0), //m6
      IntervalWithTolerance(-0.15230128443911398, 1), //m7
      IntervalWithTolerance(1.9711823426304953E-4, 1), //m8 ok
      IntervalWithTolerance(0.6570079207003687, 1), //m9
      StaticInterval(0.0) //epSS
    )
    val optimizer = GreyWolfOptimizer(function.fitness, bounds)

    val min = optimizer.min(20, 30)

    println(min)
  }
}
