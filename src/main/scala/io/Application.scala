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
      IntervalWithTolerance(8391.038944327514, 1), //a1 ok
      IntervalWithTolerance(-0.0029205021897638073, 1), //m1 ok
      IntervalWithTolerance(-0.2474891475591282, 1), //m2 ok
      IntervalWithTolerance(0.19531189181363873, 1), //m3 ok
      IntervalWithTolerance(-0.04314211922812594, 1), //m4 ok
      IntervalWithTolerance(1.0225047101330986E-4, 1), //m5
      IntervalWithTolerance(0.0), //m6
      IntervalWithTolerance(1.4944200052458E-4, 1), //m7
      IntervalWithTolerance(1.4672012304567882E-4, 1), //m8 ok
      IntervalWithTolerance(0.7877795815755233, 1), //m9
      StaticInterval(0.0) //epSS
    )
    val optimizer = GreyWolfOptimizer(function.fitness, bounds)

    val min = optimizer.min(20, 30)

    println(min)
  }
}
