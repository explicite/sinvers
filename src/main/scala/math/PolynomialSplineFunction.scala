package math

import java.util.Arrays.binarySearch

case class PolynomialSplineFunction(knots: Array[Double], polynomials: Seq[Polynomial]) {
  val intervalsCount = knots.length - 1

  def apply(x: Double): Double = {
    require(x >= knots(0) && x <= knots(intervalsCount))

    val i = binarySearch(knots, x) match {
      case bs if bs < 0                   => -bs - 2
      // This will handle the case where x is the last knot value
      // There are only n-1 polynomials, so if x is the last knot
      // then we will use the last polynomial to calculate the value.
      case bs if bs >= polynomials.length => bs - 1
      case bs                             => bs
    }

    polynomials(i).evaluate(x - knots(i))
  }
}
