package io

import java.io.File

import reo.{ HanselSpittel, HSArgs }
import test.BaseTest
import util.XORShiftRandom

class DONRegexTest extends BaseTest {
  behavior of "DON file interface"

  val randon = new XORShiftRandom()

  it should "update HS parameters" in {
    val filePath = getClass.getResource("/sym.don").getPath
    val file = new File(filePath)
    val don = DON(file)

    val hsArgs = HSArgs(
      randon.nextDouble(),
      randon.nextDouble(),
      randon.nextDouble(),
      randon.nextDouble(),
      randon.nextDouble(),
      randon.nextDouble(),
      randon.nextDouble(),
      randon.nextDouble(),
      randon.nextDouble(),
      randon.nextDouble(),
      randon.nextDouble()
    )
    don.updateHS(hsArgs)

    val hanselSpittel = HanselSpittel(file)
    val hsArgsFromDon = hanselSpittel.current

    hsArgs should equal(hsArgsFromDon)
  }
}
