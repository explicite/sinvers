package math

object Polynomial {

  /**
   * Construct a polynomial with the given coefficients.  The first element
   * of the coefficients array is the constant term.  Higher degree
   * coefficients follow in sequence.
   */
  def apply(terms: Double*): Polynomial = {
    val withDegrees = terms.zipWithIndex.map(pair => (pair._1, pair._2))
    val polys = withDegrees map {
      case (constant, degree) =>
        new Polynomial(constant, degree)
    }
    polys.reduceLeft { (aggregated, poly) =>
      aggregated.plus(poly)
    }
  }
}

// a * x^b
class Polynomial(a: Double, b: Int) {
  // coefficients
  private val coef: Array[Double] = new Array(b + 1)
  coef(b) = a
  // degree of polynomial (0 for the zero polynomial)
  private var deg = degree

  // return the degree of this polynomial (0 for the zero polynomial)
  def degree: Int = {
    var d = 0
    for (i <- 0 until coef.length if coef(i) != 0) d = i
    d
  }

  // return c = a + b
  def plus(b: Polynomial): Polynomial = {
    val a = this
    val c = new Polynomial(0, scala.math.max(a.deg, b.deg))
    for (i <- 0 to a.deg) c.coef(i) += a.coef(i)
    for (i <- 0 to b.deg) c.coef(i) += b.coef(i)
    c.deg = c.degree
    c
  }

  // return (a - b)
  def minus(b: Polynomial): Polynomial = {
    val a = this
    val c = new Polynomial(0, scala.math.max(a.deg, b.deg))
    for (i <- 0 to a.deg) c.coef(i) += a.coef(i)
    for (i <- 0 to b.deg) c.coef(i) -= b.coef(i)
    c.deg = c.degree
    c
  }

  // return (a * b)
  def times(b: Polynomial): Polynomial = {
    val a = this
    val c = new Polynomial(0, a.deg + b.deg)
    for {
      i <- 0 to a.deg
      j <- 0 to b.deg
    } c.coef(i + j) += (a.coef(i) * b.coef(j))
    c.deg = c.degree
    c
  }

  // return a(b(x))  - compute using Horner's method
  def compose(b: Polynomial): Polynomial = {
    val a = this
    var c = new Polynomial(0, 0)
    for (i <- a.deg to 0 by -1) {
      val term = new Polynomial(a.coef(i), 0)
      c = term.plus(b.times(c))
    }
    c
  }

  // do a and b represent the same polynomial?
  def equals(b: Polynomial): Boolean = {
    val a = this
    if (a.deg != b.deg)
      return false
    for (i <- a.deg to 0 by -1) {
      if (a.coef(i) != b.coef(i)) {
        return false
      }
    }
    true
  }

  // use Horner's method to compute and return the polynomial evaluated at x
  def evaluate(x: Double): Double = {
    var p = 0.0
    for (i <- deg to 0 by -1) {
      p = coef(i) + (x * p)
    }
    p
  }

  def differentiate: Polynomial =
    if (deg == 0) {
      new Polynomial(0, 0)
    } else {
      val deriv = new Polynomial(0, deg - 1)
      deriv.deg = deg - 1
      for (i <- 0 until deg)
        deriv.coef(i) = (i + 1) * coef(i + 1)
      deriv
    }

  // convert to string representation
  override def toString: String = {
    if (deg == 0) {
      "" + coef(0)
    } else if (deg == 1) {
      coef(1) + "x + " + coef(0)
    } else {
      var s: String = coef(deg) + "x^" + deg
      for (i <- deg - 1 to 0 by -1 if coef(i) == 0) {
        if (coef(i) > 0) s = s + " + " + coef(i)
        else if (coef(i) < 0) s = s + " - " + (-coef(i))
        if (i == 1) s = s + "x"
        else if (i > 1) s = s + "x^" + i
      }
      s
    }
  }
}
