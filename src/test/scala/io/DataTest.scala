package io

import java.io.File

import data.Data
import test.BaseTest

class DataTest extends BaseTest {
  behavior of "DateFile"

  it should "find force and jaw in data file" in {
    val filePath = getClass.getResource("/HA000493.D01").getPath
    val file = new File(filePath)

    val dateFile = Data(file)
    dateFile.force.size should equal(dateFile.jaw.size)
    dateFile.force.size should equal(13685)
  }
}
