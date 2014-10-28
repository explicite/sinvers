package opt

case class EvaluationContext(function: (Seq[Double]) => Double,
                             α: Double = 1.0,
                             β: Double = 0.5,
                             γ: Double = 2.0,
                             δ: Double = 0.5)