package opt

import opt.MathFunction._
import test.BaseTest

class SimplexTest extends BaseTest {
  behavior of "Simplex"

  it must "transform" in {
    implicit val context = EvaluationContext(SphereFunction)
    val simplex = Simplex(
      Seq(
        Seq(-0.5, 0.5),
        Seq(0.5, 0.5),
        Seq(-0.5, 1.5)
      )
    )

    simplex.transform.min should equal(Seq(-0.5, 0.5))
  }
}
