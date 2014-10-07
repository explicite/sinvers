package opt

import scala.concurrent.Future
import scala.math._
import io.ExecutionContext.context

object MathFunction {
  def AckleysFunction(xs: Seq[Double]) = Future {
    val x = xs(0)
    val y = xs(1)
    -20 * exp(-0.2 * sqrt(0.5 * ((x * x) + (y * y)))) - exp(0.5 * (cos(2 * Pi * x) + cos(2 * Pi * y))) + 20 + E
  }

  def SphereFunction(xs: Seq[Double]) = Future {
    xs.foldLeft(0d)((res, x) => res + (x * x))
  }

  def BealesFunction(xs: Seq[Double]) = Future{
    val x = xs(0)
    val y = xs(1)
    ((1.5 - x + (x * y)) * (1.5 - x + (x * y))) +
      ((2.25 - x + (x * y * y)) * (2.25 - x + (x * y * y))) +
      ((2.625 - x + (x * y * y * y)) * (2.625 - x + (x * y * y * y)))
  }

  def RastriginFunction(xs: Seq[Double]) = Future {
    (10d * xs.length) + xs.foldLeft(0d)((res, x) => res + ((x * x) - (10d * cos(2d * Pi * x))))
  }

  def EasomFunction(xs: Seq[Double]) = Future {
    -cos(xs(0)) * cos(xs(1)) * exp(-(((xs(0) - Pi) * (xs(0) - Pi)) + ((xs(1) - Pi) * (xs(1) - Pi))))
  }

  def McCormicFunction(xs: Seq[Double]) = Future {
    sin(xs(0) + xs(1)) + ((xs(0) - xs(1)) * (xs(0) - xs(1))) - (1.5 * xs(0)) + (2.5 * xs(1)) + 1d
  }
}
