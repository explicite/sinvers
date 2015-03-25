package io

import java.io.File

import data.DataContainer
import opt.StaticInterval
import test.BaseTest

class DataContainerTest extends BaseTest {
  behavior of "DateFile"
  val filePath = getClass.getResource("/HA000493.D01").getPath
  val file = new File(filePath)
  val data = DataContainer(file)

  it should "find force and jaw in data file" in {
    data.force.size should equal(data.jaw.size)
    data.force.size should equal(13685)
  }

}
