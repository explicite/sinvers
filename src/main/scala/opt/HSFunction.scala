package opt

import ui.view.InversView

import scala.math._

case class HSFunction(args: InversView) {

  //original function
  def apply(strain: Double): Double = {
    val InversView(_, temperature, rate, _, a1, m1, m2, m3, m4, m5, _, m7, m8, m9, _) = args
    a1 * exp(m1 * temperature) * pow(temperature, m9) * pow(strain, m2) * exp(m4 / strain) * pow(1 + strain, m5 * temperature) * exp(m7 * strain) * pow(rate, m3) * pow(rate, m8 * temperature)
  }

  def fitness(par: Seq[Double], strain: Double): Double = {
    val Seq(a1, m1, m2, m3, m4, m5, m6, m7, m8, m9, _) = par
    val temperature = args.temperature
    val rate = args.strainRate
    val props = a1 * exp(m1 * temperature) * pow(temperature, m9) * pow(strain, m2) * exp(m4 / strain) * pow(1 + strain, m5 * temperature) * exp(m7 * strain) * pow(rate, m3) * pow(rate, m8 * temperature)
    val org = apply(strain)
    pow(E, sqrt((props - org) * (props - org) + 1d))
  }

}
