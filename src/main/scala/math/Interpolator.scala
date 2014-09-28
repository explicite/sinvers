package math

/*
 * http://stackoverflow.com/questions/14330737/spline-interpolation-performance-in-scala-vs-java
 */

class NumberIsTooSmallException(xLen: Int, minimum: Int, boundIsAllowed: Boolean) extends Exception
class DimensionMismatchException(xLen: Int, yLen: Int) extends Exception

object Interpolator {
  type Real = Double

  def splineInterpolate(x: Array[Real],y: Array[Real]): PolynomialSplineFunction = {

    if (x.length != y.length)
      throw new DimensionMismatchException(x.length, y.length)

    if (x.length < 3)
      throw new NumberIsTooSmallException(x.length, 3, true)

    // Number of intervals.  The number of data points is n + 1.
    val n = x.length - 1

    // Differences between knot points
    val h = Array.tabulate(n)(i => x(i+1) - x(i))

    val mu: Array[Real] = Array.fill(n)(0)
    val z: Array[Real] = Array.fill(n + 1)(0)
    var i = 1
    while (i < n) {
      val g = 2.0 * (x(i+1) - x(i-1)) - h(i-1) * mu(i-1)
      mu(i) = h(i) / g
      z(i) = (3.0 * (y(i+1) * h(i-1) - y(i) * (x(i+1) - x(i-1))+ y(i-1) * h(i)) /
        (h(i-1) * h(i)) - h(i-1) * z(i-1)) / g
      i += 1
    }

    // cubic spline coefficients --  b is linear, c quadratic, d is cubic (original y's are constants)
    val b: Array[Real] = Array.fill(n)(0)
    val c: Array[Real] = Array.fill(n + 1)(0)
    val d: Array[Real] = Array.fill(n)(0)

    var j = n-1
    while (j >= 0) {
      c(j) = z(j) - mu(j) * c(j + 1)
      b(j) = (y(j+1) - y(j)) / h(j) - h(j) * (c(j+1) + 2.0 * c(j)) / 3.0
      d(j) = (c(j+1) - c(j)) / (3.0 * h(j))
      j -= 1
    }

    val polynomials = for (i <- 0 until n) yield Polynomial(y(i), b(i), c(i), d(i))
    new PolynomialSplineFunction(x, polynomials)
  }
}
