package opt

import opt.MathFunction._
import org.scalameter.api._

object Benchmark extends PerformanceTest.OfflineRegressionReport {
/*  val wolfs = for {
    size <- Gen.range("wolfs")(100, 500, 100)
  } yield 0 until size

  val iterations = 500
  val dim = 3
  val ε = 0.1
  val min = SphereFunction(Seq.fill(dim)(0d))
  val gwo = new GreyWolfOptimizer(SphereFunction, Seq.fill(dim)(Bounds(-5d, 5d)))


  measure method "GreyWolfOptimizer" in {
    using(wolfs) curve "Wolfs" in {
      wolf =>
        gwo.min(wolf.size * dim, iterations * dim)
    }
  }

  override def reporter: Reporter = Reporter.Composite(
    new RegressionReporter(
      RegressionReporter.Tester.OverlapIntervals(),
      RegressionReporter.Historian.ExponentialBackoff()
    ), HtmlReporter(embedDsv = true)
  )*/
}
