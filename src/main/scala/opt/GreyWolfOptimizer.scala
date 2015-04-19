package opt

import scala.math.abs

case class GreyWolfOptimizer[T <: Interval](f: (Seq[Double]) => Double, bounds: Seq[T]) {
  val dimX = bounds.length
  val random = new java.security.SecureRandom()

  /**
   * Find minimum
   *
   * @param actors number of search actors
   * @param iterations number of iterations
   *
   * @return minimum (best position)
   */
  def min(actors: Int, iterations: Int): Seq[Double] = optimize(actors, iterations)(MIN)

  /**
   * Find maximum
   *
   * @param actors number of search actors
   * @param iterations number of iterations
   *
   * @return optimum (best position)
   */
  def max(actors: Int, iterations: Int): Seq[Double] = optimize(actors, iterations)(MAX)

  private def optimize(numberOfActors: Int, iterations: Int)(opt: Optimum): Seq[Double] = {
    val dimY = numberOfActors

    val alpha = Wolf(List.fill(dimX)(0d), opt.inf)
    val beta = Wolf(List.fill(dimX)(0d), opt.inf)
    val delta = Wolf(List.fill(dimX)(0d), opt.inf)

    val positions: List[Double] = List.fill(dimX * dimY)(0d).zipWithIndex.map { case (_, index) => bounds(index % dimX).next }

    def step(iteration: Int)(context: Context): Context = {
      if (iteration < iterations) step(iteration + 1)(context.evolution(2d - iteration * (2d / iterations))) else context
    }

    step(0)(Context(alpha, beta, delta, positions)).alphaOrg.pos
  }

  case class Wolf(pos: List[Double], score: Double) extends Ordered[Wolf] {
    def compare(that: Wolf): Int = this.score compare that.score
  }

  case class Context(alphaOrg: Wolf,
      betaOrg: Wolf,
      deltaOrg: Wolf,
      positions: List[Double]) {

    def evolution(a: Double): Context = {
      val evaluated = evaluate
      evaluated.copy(
        positions = positions.zipWithIndex.map {
          case (value, index) =>
            val x = index % dimX
            val cAlpha = Coefficient(a)
            val dAlpha = abs(cAlpha.y * evaluated.alphaOrg.pos(x) - value)
            val x1 = evaluated.alphaOrg.pos(x) - cAlpha.x * dAlpha

            val cBeta = Coefficient(a)
            val dBeta = abs(cBeta.y * evaluated.betaOrg.pos(x) - value)
            val x2 = evaluated.betaOrg.pos(x) - cBeta.x * dBeta

            val cDelta = Coefficient(a)
            val dDelta = abs(cDelta.y * evaluated.deltaOrg.pos(x) - value)
            val x3 = evaluated.deltaOrg.pos(x) - cDelta.x * dDelta

            (x1 + x2 + x3) / 3d
        }
      )
    }

    private def rebirth(positions: List[Double]): List[Double] = {
      positions.zipWithIndex.map {
        case (value, index) =>
          val interval = bounds(index % dimX)
          if (value > interval.max || value < interval.min) interval.next else value
      }
    }

    private def evaluate: Context = {
      val evaluated = rebirth(positions).grouped(dimX).toParArray.map {
        position => (position, f(position))
      }.toList
      val propositions = evaluated.sortBy(_._2).take(3).map { case (position, value) => Wolf(position, value) }

      def reg(Alpha: Wolf,
        Beta: Wolf,
        Delta: Wolf)(propositions: List[Wolf]): Context = {
        propositions match {
          case head :: tail =>
            val alpha = if (head < alphaOrg) head else Alpha
            val beta = if (head > alpha && head < betaOrg) head else Beta
            val delta = if (head > alpha && head > beta && head < deltaOrg) head else Delta
            reg(alpha, beta, delta)(tail)
          case _ => Context(Alpha, Beta, Delta, evaluated.map(_._1).flatten)
        }
      }
      reg(alphaOrg, betaOrg, deltaOrg)(propositions)
    }

  }

  case class Coefficient(x: Double, y: Double)

  object Coefficient {
    def apply(i: Double): Coefficient = Coefficient(2d * i * random.nextDouble() - 1d, 2d * random.nextDouble())
  }
}
