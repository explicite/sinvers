package io

import opt.{ GreyWolfOptimizer, StaticInterval }

import scala.compat.Platform
import scala.concurrent.duration._

object GWOApplication extends Application {
  def main(args: Array[String]): Unit = {
    val bounds = Seq(
      StaticInterval(1200, 1600), //a1
      StaticInterval(-0.002, -0.003), //m1
      StaticInterval(-0.1, -0.2), //m2
      StaticInterval(0.12, 0.16), //m3
      StaticInterval(-0.04, -0.065), //m4
      StaticInterval(0.0), //m5
      StaticInterval(0.0), //m6
      StaticInterval(0.0), //m7
      StaticInterval(0.0), //m8
      StaticInterval(0.0), //m9
      StaticInterval(0.0) //epSS
    )

    def fit(sx: Seq[Double]): Double = {
      function.fitness(sx)
    }
    val optimizer = GreyWolfOptimizer(fit, bounds)
    val start = Platform.currentTime
    val min = optimizer.min(100, 50)
    val stop = Platform.currentTime
    val duration = Duration(stop - start, MICROSECONDS)

    println(s"Computation in: ${duration.toSeconds}sec \n$min")
  }
}
