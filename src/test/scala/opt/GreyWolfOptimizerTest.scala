package opt

import opt.MathFunction._
import org.scalameter.api._
import test.BaseTest

import scala.math.Pi


class GreyWolfOptimizerTest extends BaseTest {
  behavior of "GreyWolfOptimizer"

  val wolfs = 100
  val iterations = 5000
  val dim = 3
  val ε = 0.001

  it must "find min for Ackley's function over [-5, 5]" in {
    val min = AckleysFunction(Seq(0, 0))
    val gwo = new GreyWolfOptimizer(AckleysFunction, Seq(StaticInterval(-5d, 5d), StaticInterval(-5d, 5d)))
    AckleysFunction(gwo.min(wolfs, iterations)._1) should equal(min +- ε)
  }

  it must "find min for Sphere function over [-5, 5]" in {
    val min = SphereFunction(Seq.fill(dim)(0d))
    val gwo = new GreyWolfOptimizer(SphereFunction, Seq.fill(dim)(StaticInterval(-5d, 5d)))
    SphereFunction(gwo.min(wolfs * dim, iterations * dim)._1) should equal(min +- ε)
  }

  it must "find min for Beale's function over [-4.5, 4.5]" in {
    val min = BealesFunction(Seq(3d, 0.5))
    val gwo = new GreyWolfOptimizer(BealesFunction, Seq(StaticInterval(-4.5, 4.5), StaticInterval(-4.5, 4.5)))
    BealesFunction(gwo.min(wolfs, iterations)._1) should equal(min +- ε)
  }

  it must "find min for Rastrigin function over [-5.12, 5.12]" in {
    val min = RastriginFunction(Seq.fill(dim)(0d))
    val gwo = new GreyWolfOptimizer(RastriginFunction, Seq.fill(dim)(StaticInterval(-5.12, 5.12)))
    RastriginFunction(gwo.min(wolfs * dim, iterations * dim)._1) should equal(min +- ε)
  }

  it must "find min for Eosom function over [-100, 100]" in {
    val min = EasomFunction(Seq(Pi, Pi))
    val gwo = new GreyWolfOptimizer(EasomFunction, Seq(StaticInterval(-100d, 100d), StaticInterval(-100d, 100d)))
    EasomFunction(gwo.min(wolfs, iterations)._1) should equal(min +- ε)
  }

  it must "find min for McCormick function over [-1.5, 4] [-3, 4]" in {
    val min = McCormicFunction(Seq(-0.54719, -1.54719))
    val gwo = new GreyWolfOptimizer(McCormicFunction, Seq(StaticInterval(-1.5, 4d), StaticInterval(-3d, 4d)))
    McCormicFunction(gwo.min(wolfs, iterations)._1) should equal(min +- ε)
  }

}
