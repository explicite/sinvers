package util

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ duration, Await, Future }

/**
 * Kolmogorov-Zurbenko low-pass linear filter.
 * <p>KZ is an iterated moving average. The filter can be used with missing values.</p>
 * <p>Zurbenko, I. G., 1986: The spectral Analysis of Time Series. North-Holland, 248 pp.</p>
 */
object KZ {
  /**
   * Filter data
   *
   * @param x a vector of the time series
   * @param m window size for the filter
   * @param k number of iterations
   *
   * @return smoothed data
   *
   */
  def apply(x: Seq[Double], m: Double, k: Int): Seq[Double] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val window: Int = StrictMath.floor(m / 2.0).toInt

    var tmp: ArrayBuffer[Double] = ArrayBuffer[Double]() ++= x
    val ans: ArrayBuffer[Double] = ArrayBuffer.fill(x.length)(0.0)

    for (k <- 0 until k) {
      val futures = (0 until x.length).map(i => Future { ans(i) = mavg1d(tmp, i, window) })
      Await.result(Future.sequence(futures), duration.Duration.Inf)
      tmp = ans.clone()
    }

    ans
  }

  def mavg1d(x: Seq[Double], c: Int, w: Int): Double = {
    var s: Double = 0.0
    val start = if (c - w > 0) c - w else 0
    val end = if (c + w < x.length) c + w + 1 else x.length

    for (i <- start until end) s += x(i)

    s / (end - start)
  }

}