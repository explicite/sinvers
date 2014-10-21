package opt

import scala.collection.mutable.{Seq => MutableSeq}
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.math.abs
import scalax.chart.module.Charting

case class GreyWolfOptimizer[T <: Interval](f: (Seq[Double]) => Double, b: Seq[T]) extends Charting {

  import io.ExecutionContext.context

  val dim = b.length
  val random = new java.security.SecureRandom()

  /**
   * Find minimum
   *
   * @param a number of search actors
   * @param i number of iterations
   *
   * @return minimum (best position, best score)
   */
  def min(a: Int, i: Int): (Seq[Double], Double) = optimize(a, i)(MIN)

  /**
   * Find maximum
   *
   * @param a number of search actors
   * @param i number of iterations
   *
   * @return optimum (best position, best score)
   */
  def max(a: Int, i: Int): (Seq[Double], Double) = optimize(a, i)(MAX)

  private def optimize(numberOfActors: Int, iterations: Int)(opt: Optimum): (Seq[Double], Double) = {
    //TODO
    val alphaSeries = Seq[(Double, Double)]() toXYSeries "alpha"

    val chart = XYLineChart(alphaSeries)
    chart.show()
    var alphaPos: Seq[Double] = Seq.fill(dim)(0d)
    var alphaScore: Double = opt.inf
    var betaPos: Seq[Double] = Seq.fill(dim)(0d)
    var betaScore: Double = opt.inf
    var deltaPos: Seq[Double] = Seq.fill(dim)(0d)
    var deltaScore: Double = opt.inf

    val positions: MutableSeq[MutableSeq[Double]] = {
      val positions: MutableSeq[MutableSeq[Double]] = MutableSeq.fill(numberOfActors)(MutableSeq.fill(dim)(0.0))

      positions.foreach(position => {
        for (dim <- 0 until dim) {
          position(dim) = b(dim).next
        }
      })

      positions
    }

    // Main loop
    var iteration: Int = 0
    def reorganize(position: Int) {
      //Return back the search agents that go beyond the boundaries of the search space
      positions(position) = backToSpace(positions(position))

      // Calculate objective function for each search actors
      val fitness: Double = f(positions(position))

      scala.concurrent.blocking {
        // Update Alpha, Beta, and Delta
        if (fitness < alphaScore) {
          alphaScore = fitness
          alphaPos = positions(position).clone()
        }

        if (fitness > alphaScore && fitness < betaScore) {
          betaScore = fitness
          betaPos = positions(position).clone()
        }

        if (fitness > alphaScore && fitness > betaScore && fitness < deltaScore) {
          deltaScore = fitness
          deltaPos = positions(position).clone()
        }
      }
    }

    while (iteration < iterations) {
      val futures = positions.zipWithIndex.map {
        case (_, index) => Future {
          reorganize(index)
        }
      }

      Await.result(Future.sequence(futures), Duration.Inf)

      val a: Double = 2d - iteration * (2d / iterations)

      var i: Int = 0
      while (i < positions.length) {
        var j: Int = 0
        while (j < positions(i).length) {
          val cAlpha = Coefficient(a)
          val dAlpha = abs(cAlpha.y * alphaPos(j) - positions(i)(j))
          val x1 = alphaPos(j) - cAlpha.x * dAlpha

          val cBeta = Coefficient(a)
          val dBeta = abs(cBeta.y * betaPos(j) - positions(i)(j))
          val x2 = betaPos(j) - cBeta.x * dBeta

          val cDelta = Coefficient(a)
          val dDelta = abs(cDelta.y * deltaPos(j) - positions(i)(j))
          val x3 = deltaPos(j) - cDelta.x * dDelta

          positions(i)(j) = (x1 + x2 + x3) / 3d
          j += 1
        }
        i += 1
      }

      //TODO add chart
      alphaSeries.add(iteration, alphaScore)

      iteration += 1
    }

    (alphaPos, alphaScore)
  }

  private def backToSpace(p: MutableSeq[Double]): MutableSeq[Double] = {
    p.zip(b).map(ab => if (ab._1 > ab._2.max || ab._1 < ab._2.min) ab._2.next else ab._1)
  }

  case class Coefficient(x: Double, y: Double)

  object Coefficient {
    def apply(i: Double): Coefficient = Coefficient(2d * i * random.nextDouble() - 1d, 2d * random.nextDouble())
  }

}
