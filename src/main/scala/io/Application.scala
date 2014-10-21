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
      IntervalWithTolerance(3918.5095068309856, 0.2), //a1 ok
      IntervalWithTolerance(-0.001435642018064198, 0.5), //m1 ok
      IntervalWithTolerance(-0.17569654414715932, 0.5), //m2 ok
      IntervalWithTolerance(0.034145892799436285, 0.5), //m3 ok
      IntervalWithTolerance(-0.03798843952823311, 0.5), //m4 ok
      StaticInterval(-1.971630866804775E-5, 1.971630866804775E-5), //m5
      StaticInterval(0.0), //m6
      StaticInterval(-9.370732092494505E-5, 9.370732092494505E-5), //m7
      StaticInterval(-3.801271044010449E-5, 3.801271044010449E-5), //m8 ok
      IntervalWithTolerance(0.5987121097007685, 0.5), //m9
      StaticInterval(0.0) //epSS
    )
    val optimizer = GreyWolfOptimizer(function.fitness, bounds)

    val min = optimizer.min(20, 50)

    println(min)
  }
}
