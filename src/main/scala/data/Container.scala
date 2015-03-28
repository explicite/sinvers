package data

import math.PolynomialSplineFunction
import opt.Interval

trait Container {
  type T

  def toPrint: Seq[String]

  def slice(interval: Interval): T

  def filter(k: Double, m: Int): T

  val interpolator: PolynomialSplineFunction
}
