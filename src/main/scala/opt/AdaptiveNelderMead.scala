package opt

/**
 * @param f function to minimize
 */
case class AdaptiveNelderMead(f: (Seq[Double]) => Double) {

  /**
   * @param ε error
   */
  def minimize(proposal: Seq[Double],
    ε: Double = 0.01): Seq[Double] = {

    val dimension = proposal.size
    val α: Double = 1.0
    val β: Double = 0.5 //1.0 + (2.0 / dimension)
    val γ: Double = 2.0 //0.75 - (1.0 / (2.0 * dimension))
    val δ: Double = 0.5 //1 - (1 / dimension)

    implicit val context = EvaluationContext(f, α, β, γ, δ)

    def iteration(simplex: Simplex): Simplex = {
      if (simplex.transformable(ε)) iteration(simplex.transform) else simplex
    }

    val points = generate(proposal)

    val simplex = Simplex(points)

    iteration(simplex).min
  }

  private[this] def generate(proposal: Seq[Double]): Seq[Seq[Double]] = {
    (for (i <- proposal.indices) yield {
      proposal.zipWithIndex.map {
        case (element, index) => if (index == i) element * 1.0 else element * 0.00025
      }
    }) :+ proposal
  }
}
