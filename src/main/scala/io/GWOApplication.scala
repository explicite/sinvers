package io

import opt.{ GreyWolfOptimizer, StaticInterval }
import util.Util

object GWOApplication extends Application with App {
  override def main(args: Array[String]): Unit = {
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
    val wolfs = 200
    val iterations = 50
    Util.time {
      progress ! ui.controls.ProgressBarProtocol.Set(System.nanoTime(), wolfs * iterations)
      val min = optimizer.min(wolfs, iterations)
      println(s"minumum: $min")
    }

    system.shutdown()
  }
}
