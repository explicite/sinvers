package opt

import ui.view.InversView

import scala.math._

case class HSFunction(args: InversView) {

  //original function
  private def original(strain: Double): Double = {
    val InversView(_, temperature, rate, a1, m1, m2, m3, m4, m5, _, m7, m8, m9, _) = args
    a1 * exp(m1 * temperature) * pow(temperature, m9) * pow(strain, m2) * exp(m4 / strain) * pow(1 + strain, m5 * temperature) * exp(m7 * strain) * pow(rate, m3) * pow(rate, m8 * temperature)
  }

  def fitness(a1: Double,
    m1: Double,
    m2: Double,
    m3: Double,
    m4: Double,
    m5: Double,
    m6: Double,
    m7: Double,
    m8: Double,
    m9: Double,
    strain: Double): Double = {
    val temperature = args.temperature
    val rate = args.strainRate
    val props = a1 * exp(m1 * temperature) * pow(temperature, m9) * pow(strain, m2) * exp(m4 / strain) * pow(1 + strain, m5 * temperature) * exp(m7 * strain) * pow(rate, m3) * pow(rate, m8 * temperature)
    val org = original(strain)
    sqrt((props - org) * (props - org))
  }

}
