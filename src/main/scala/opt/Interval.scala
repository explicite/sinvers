package opt

import util.XORShiftRandom

trait Interval {
  private val random = new XORShiftRandom()

  protected val d1: Double
  protected val d2: Double

  def min = if (d1 <= d2) d1 else d2
  def max = if (d1 >= d2) d1 else d2

  def next: Double = random.nextDouble(min, max)
}

case class StaticInterval(private val b1: Double, private val b2: Double) extends Interval {
  override val d1 = b1
  override val d2 = b2
}

object StaticInterval {
  def apply(d1: Double): StaticInterval = StaticInterval(d1, d1)
}

case class IntervalWithTolerance(middle: Double, tolerance: Double = 0d) extends Interval {
  override val d1 = middle - (middle * tolerance)
  override val d2 = middle + (middle * tolerance)
}
