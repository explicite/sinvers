package io

import opt.AdaptiveNelderMead

object NMApplication extends Application {
  def main(args: Array[String]) = {

    val point = Seq(
      1417.6201699613532,
      -0.00279965209745294,
      -0.0784544846296892,
      0.10863523708720269,
      -0.041223045962516254,
      1.0225047101330986E-4,
      0.0,
      -0.15094992716859068,
      1.4944200052458E-4,
      0.7877795815755233
    )

    val nelderMeadOptimizer = AdaptiveNelderMead(function.fitness)
    val min = nelderMeadOptimizer.minimize(point, 0.001)

    println(min)
  }
}
