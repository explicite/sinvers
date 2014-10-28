package opt

import opt.MathFunction._
import test.BaseTest

class NelderMeadTest extends BaseTest {
  behavior of "NelderMead"
  val dim = 3
  val ε = 0.001

  it must "find min for Sphere function over [-5, 5]" in {
    val min = SphereFunction(Seq.fill(dim)(0d))
    val nelderMead = NelderMead(SphereFunction)
    val points = Seq(Seq(-5d, -5d), Seq(-5d, 5d), Seq(2.5, 5d))
    SphereFunction(nelderMead.minimize(points, ε)) should equal(min +- ε)
  }

}
