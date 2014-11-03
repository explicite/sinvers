def fun(i: Int) = {
  (0 until 1000000) foreach (i => i*i*i*i*i*i*i)
}
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

val cached = Cached(fun)
(0 to 100000) foreach(_ => fun(1))
(0 to 100000) foreach(_ => cached(1))