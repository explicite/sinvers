package io

import java.io.File

import reo.{HSArgs, HanselSpittel}
import test.BaseTest

class HanselSpittelTest extends BaseTest {
  behavior of "Hansel Spittel Test"

  val filePath = getClass.getResource("/sym.don").getPath
  val file = new File(filePath)
  val hanselSpittel = HanselSpittel(file)

  it should "create Hansel-Spittel from file" in {
    val hsArgs = hanselSpittel.current

    hsArgs.a1 should equal(1271649.000000)
    hsArgs.m1 should equal(-0.00254)
    hsArgs.m2 should equal(-0.05621)
    hsArgs.m3 should equal(0.1455)
    hsArgs.m4 should equal(-0.0324)
    hsArgs.m5 should equal(0)
    hsArgs.m6 should equal(0)
    hsArgs.m7 should equal(0)
    hsArgs.m8 should equal(0)
    hsArgs.m9 should equal(0)
  }

  it should "update Hansel-Spittel in file" in {
    val hsArgs = HSArgs(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
    hanselSpittel.update(hsArgs)

    val currentHsArgs = hanselSpittel.current
    hsArgs should equal(currentHsArgs)
  }

}
