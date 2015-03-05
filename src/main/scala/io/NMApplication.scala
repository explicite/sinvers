package io

import opt.AdaptiveNelderMead

object NMApplication extends Application {
  def main(args: Array[String]) = {

    val point = Seq(
      1400d,
      -0.002,
      -0.1,
      0.12,
      -0.04
    )

    val nelderMeadOptimizer = AdaptiveNelderMead(function.fitness)
    val min = nelderMeadOptimizer.minimize(point, 1e-12)

    println(min)
  }
}
