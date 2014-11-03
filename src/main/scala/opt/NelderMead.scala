package opt

/**
 * @param f
 * @param α
 * @param β
 * @param γ
 * @param δ
 */
case class NelderMead(f: (Seq[Double]) => Double,
                      α: Double = 1.0,
                      β: Double = 0.5,
                      γ: Double = 2.0,
                      δ: Double = 0.5) {

  /**
   * @param ε error
   */
  def minimize(points: Seq[Seq[Double]],
          ε: Double = 0.01): Seq[Double] = {

    implicit val context = EvaluationContext(f, α, β, γ, δ)


    def iteration(simplex: Simplex): Simplex = {
      //if(simplex.transformable(ε)) iteration(simplex.transform) else simplex
      if(context.function(simplex.min) > ε) iteration(simplex.transform) else simplex
    }

    val simplex = Simplex(points)

    iteration(simplex).min
  }


}
