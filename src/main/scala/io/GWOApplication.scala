package io

import opt.{ GreyWolfOptimizer, IntervalWithTolerance, StaticInterval }

import scala.compat.Platform
import scala.concurrent.duration._

object GWOApplication extends Application {
  def main(args: Array[String]): Unit = {
    val bounds = Seq(
      StaticInterval(1400, 1600), //a1
      StaticInterval(-0.002, -0.003), //m1
      StaticInterval(-0.1, -0.2), //m2
      StaticInterval(0.12, 0.16), //m3
      StaticInterval(-0.04, -0.065), //m4
      StaticInterval(1.0225047101330986E-4), //m5
      StaticInterval(0.0), //m6
      StaticInterval(-0.15094992716859068, -0.14590451222105483), //m7
      StaticInterval(0, 1.4944200052458E-4), //m8
      StaticInterval(0.5, 0.8), //m9
      StaticInterval(0.0) //epSS
    )

    def fit(sx: Seq[Double]): Double = {
      function.fitness(sx)
    }
    val optimizer = GreyWolfOptimizer(fit, bounds)
    val start = Platform.currentTime
    val min = optimizer.min(100, 100)
    val stop = Platform.currentTime
    val duration = Duration(stop - start, MICROSECONDS)

    println(s"Computation in: ${duration.toSeconds}sec \n$min")
  }
}
