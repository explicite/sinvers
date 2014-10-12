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
      IntervalWithTolerance(20000, 1), //a1 ok
      IntervalWithTolerance(-0.0011415601892736061, 5), //m1 ok
      IntervalWithTolerance(-0.31645294641921773, 5), //m2 ok
      IntervalWithTolerance(0.0396536972818135, 5), //m3 ok
      IntervalWithTolerance(-0.05968658294035687, 5), //m4 ok
      IntervalWithTolerance(1.7824317209455084E-5, 5), //m5
      IntervalWithTolerance(0.0), //m6
      IntervalWithTolerance(7.322474544897119E-5, 5), //m7
      IntervalWithTolerance(4.933482677015285E-5, 5), //m8 ok
      IntervalWithTolerance(0.5247387458805934, 5), //m9
      StaticInterval(0.0) //epSS
    )
    val optimizer = GreyWolfOptimizer(function.fitness, bounds)

    val min = optimizer.min(100, 20)

    println(min)
  }
}
