package opt

import test.BaseTest

class IntervalTest extends BaseTest {
  behavior of "Interval"

  it must "return min and max in static interval" in {
    val interval = StaticInterval(-2, -23)
    interval.max should equal(-2)
    interval.min should equal(-23)
  }

  it must "return min and max in interval with tolerance" in {
    val interval = IntervalWithTolerance(-20, 0.5)
    interval.max should equal(-10)
    interval.min should equal(-30)
  }

}
