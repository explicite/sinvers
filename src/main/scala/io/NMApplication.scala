package io

import opt.AdaptiveNelderMead


object NMApplication extends Application {
  def main(args: Array[String]) = {

    val point = Seq(1417.6201699613532, -0.00279965209745294, -0.0784544846296892, 0.10863523708720269, -0.041223045962516254)

    val nelderMeadOptimizer = AdaptiveNelderMead(function.fitness)
    val min = nelderMeadOptimizer.minimize(point, 0.001)

    println(min)
  }
}
