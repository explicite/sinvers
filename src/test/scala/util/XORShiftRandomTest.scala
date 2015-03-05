package util

import test.BaseTest

class XORShiftRandomTest extends BaseTest {
  behavior of "XORShiftRandom"
  val random = new XORShiftRandom()

  it must "generate random numbers for som interval" in {
    val randoms = (0 until 200000).map { index => random.nextDouble(-2.321312, 1.3232) }
    randoms.distinct.size should be(200000)

    randoms.forall(random => random >= -2.321312 && random <= 1.3232) should equal(true)
  }

  it must "generate random numbers for som interval with same bounds" in {
    val randoms = (0 until 200000).map { index => random.nextDouble(2d, 2d) }
    randoms.distinct.size should be(1)

    randoms.forall(random => random >= 2d && random <= 2d) should equal(true)
  }

}
