package opt

import scala.collection.mutable.{Seq => MutableSeq}

case class NelderMead(f: (Seq[Double]) => Double) {

  /**
   * @param α coefficient of reflection
   * @param β coefficient of narrowing
   * @param γ coefficient of expansion greater than 2
   * @param δ coefficient of reduction
   * @param ε error
   */
  def min(points: MutableSeq[MutableSeq[Double]],
          α: Double = 1.0,
          β: Double = 0.5,
          γ: Double = 2.0,
          δ: Double = 0.5,
          ε: Double = 0.01) = {

    var pi = points
    def values = pi.map(f)
    def max = pi(values.indexOf(values.max))
    def min = pi(values.indexOf(values.min))

    repeat {
      def p = pi.filter(_ != max).foldLeft(MutableSeq.fill(pi.head.size)(0d))((r, c) => r.zip(c) map { case (re, ce) => re + ce}) map (_ / (pi.size - 1))

      val pRef = p.zip(p.zip(max) map { case (re, ce) => (re - ce) * α}) map { case (re, ce) => re - ce}
      if (f(pRef) < f(min)) {
        val pe = p.zip(pRef.zip(p) map { case (re, ce) => (re - ce) * γ}) map { case (re, ce) => re + ce}
        if (f(pe) < f(pRef)) {
          pi(values.indexOf(values.max)) = pe
        } else {
          pi(values.indexOf(values.max)) = pRef
        }
      } else {
        val pz = p.zip(max.zip(p) map { case (re, ce) => (re - ce) * β}) map { case (re, ce) => re + ce}
        if (f(pz) >= f(max)) {
          pi = pi.map(point => if (point != min) point.zip(min) map { case (re, ce) => (re + ce) * δ} else point)
        } else {
          pi(values.indexOf(values.max)) = pz
        }
      }
      println(min)
    } until pi.filter(_ != min).forall(p => f(p) - f(min) < ε)

    min
  }

  def repeat(body: => Unit) = new {
    def until(condition: => Boolean) = {
      do {
        body
      } while (!condition)
    }
  }
}


