package opt

import opt.MathFunction._
import test.BaseTest

import scala.collection.mutable.{Seq => MutableSeq}

class NelderMeadTest extends BaseTest {
  behavior of "NelderMead"
  val ε = 0.001
  val dim = 2


  it must "find min for Sphere function over [-5, 5]" in {
    val min = SphereFunction(Seq.fill(dim)(0d))
    val nm = NelderMead(SphereFunction)
    val points = MutableSeq(
      MutableSeq(-0.5, 0.5),
      MutableSeq(0.5, 0.5),
      MutableSeq(-0.5, 1.5)
    )
    SphereFunction(nm.min(points)) should equal(min +- ε)
  }

}
