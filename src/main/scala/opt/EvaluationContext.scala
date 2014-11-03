package opt

case class EvaluationContext(private val fun: (Seq[Double]) => Double,
                             α: Double = 1.0,
                             β: Double = 0.5,
                             γ: Double = 2.0,
                             δ: Double = 0.5) {
  def function = Cached(fun)

  class Cached[-I, +O](f: I => O) extends (I => O) {
    private[this] val cache = scala.collection.concurrent.TrieMap.empty[I, O]

    def apply(x: I): O = {
      if (cache.contains(x)) {
        cache(x)
      } else {
        val y = f(x)
        cache += (x -> y)
        y
      }
    }
  }

  object Cached {
    def apply[I, O](f: I => O) = new Cached(f)
  }

}