package data

import math.PolynomialSplineFunction
import opt.Interval

case class ResultContainer(time: Seq[Double],
    force: Seq[Double],
    jaw: Seq[Double],
    velocity: Seq[Double]) {

  def fit(interpolator: PolynomialSplineFunction, interval: Interval): Double = {
    if (valid(interval)) {
      val (forceToInter, jawToInter) = sections(force.zip(jaw), 5).unzip
      val interpolatedForce = jawToInter.map(interpolator.apply)
      val result = forceToInter.zip(interpolatedForce).map {
        case (c, ii) => scala.math.pow(scala.math.E, math.sqrt {
          (c - ii) * (c - ii) + 1
        }.toDouble)
      }.sum / forceToInter.size

      result - scala.math.E
    } else {
      Double.MaxValue
    }
  }

  def valid(interval: Interval): Boolean = force.size >= 30

  private def sections(sx: Seq[(Double, Double)], slices: Int): List[(Double, Double)] = {
    val (_, jaw) = sx.unzip
    val min = jaw.min
    val max = jaw.max
    val span = (max - min) / slices
    val splits = List.iterate(min, slices)(_ + span) :+ max
    splits.map { s => sx.find { case (f, j) => j < s + span && j > s - span } }.flatten.distinct
  }

}

object ResultContainer {
  val empty = ResultContainer(Nil, Nil, Nil, Nil)
}