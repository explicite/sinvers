package opt

import opt.MathFunction._
import test.BaseTest

import scala.math._

class AdaptiveNelderMeadTest extends BaseTest {
  behavior of "NelderMead"
  val dim = 3

  it must "find min for Ackley's function" in {
    val ε = 0.001
    val min = AckleysFunction(Seq(0d, 0d))
    val nelderMead = AdaptiveNelderMead(AckleysFunction)
    val point = Seq(0.2, -0.1)
    AckleysFunction(nelderMead.minimize(point, ε)) should equal(min +- ε)
  }

  it must "find min for Sphere function" in {
    val ε = 0.001
    val min = SphereFunction(Seq.fill(dim)(0d))
    val nelderMead = AdaptiveNelderMead(SphereFunction)
    val point = Seq(-1d, 1d)
    SphereFunction(nelderMead.minimize(point, ε)) should equal(min +- ε)
  }

  it must "find min for Beale's function" in {
    val ε = 0.001
    val min = BealesFunction(Seq(3d, 0.5))
    val nelderMead = AdaptiveNelderMead(BealesFunction)
    val point = Seq(2.8d, 0.3d)
    BealesFunction(nelderMead.minimize(point, ε)) should equal(min +- ε)
  }

  it must "find min for Rastrigin function" in {
    val ε = 0.001
    val min = RastriginFunction(Seq.fill(dim)(0d))
    val nelderMead = AdaptiveNelderMead(RastriginFunction)
    val point = Seq(-1d, 1d)
    RastriginFunction(nelderMead.minimize(point, ε)) should equal(min +- ε)
  }

  it must "find min for Eosom function" in {
    val ε = 0.001
    val min = EasomFunction(Seq(Pi, Pi))
    val nelderMead = AdaptiveNelderMead(EasomFunction)
    val point = Seq(4d, 4d)
    EasomFunction(nelderMead.minimize(point, ε)) should equal(min +- ε)
  }

  it must "find min for McCormick function" in {
    val ε = 0.001
    val min = McCormicFunction(Seq(-0.54719, -1.54719))
    val nelderMead = AdaptiveNelderMead(McCormicFunction)
    val point = Seq(-1d, -1d)
    McCormicFunction(nelderMead.minimize(point, ε)) should equal(min +- ε)
  }

  it must "find min for Booth function" in {
    val ε = 0.001
    val min = BoothFunction(Seq(1, 3))
    val nelderMead = AdaptiveNelderMead(BoothFunction)
    val point = Seq(2d, -3d)
    BoothFunction(nelderMead.minimize(point, ε)) should equal(min +- ε)
  }

  ignore must "find min for Bukin function" in {
    val ε = 0.001
    val min = BukinFunction(Seq(-10.001, 0.99))
    val nelderMead = AdaptiveNelderMead(BukinFunction)
    val point = Seq(-10d, 1d)
    BukinFunction(nelderMead.minimize(point, ε)) should equal(min +- ε)
  }

  ignore must "find min for Levi function" in {
    val ε = 0.001
    val min = LeviFunction(Seq(1, 1))
    val nelderMead = AdaptiveNelderMead(LeviFunction)
    val point = Seq(2d, 0.5)
    LeviFunction(nelderMead.minimize(point, ε)) should equal(min +- ε)
  }

}
