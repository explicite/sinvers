package io

import opt.{ GreyWolfOptimizer, IntervalWithTolerance, StaticInterval }

object GWOApplication extends Application {
  def main(args: Array[String]): Unit = {
    val bounds = Seq(
      IntervalWithTolerance(1232.9863, 0.5), //a1
      IntervalWithTolerance(-0.00254, 0.5), //m1
      IntervalWithTolerance(-0.05621, 0.5), //m2
      IntervalWithTolerance(0.1455, 0.5), //m3
      IntervalWithTolerance(-0.0324, 0.5), //m4
      StaticInterval(0.0), //m5
      StaticInterval(0.0), //m6
      StaticInterval(0.0), //m7
      StaticInterval(0.0), //m8
      StaticInterval(0.0), //m9
      StaticInterval(0.0) //epSS
    )

    val optimizer = GreyWolfOptimizer(function.fitness, bounds)
    val min = optimizer.min(50, 10)

    println(min)
  }
}
