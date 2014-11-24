package io

import data.DataFile
import opt.InversFunction

trait Application {
  val fx2Dir = "C:\\Users\\Jan\\Desktop\\Forge2-V3.0"
  val workingDirectory = "C:\\Users\\Jan\\Desktop\\sym"
  val process = Forge(fx2Dir)

  val experimentDirectory = "C:\\Users\\Jan\\Desktop\\computed.txt"
  val don = DON(new java.io.File(s"$workingDirectory\\newSym.don"))
  val experimentData = DataFile(new java.io.File(experimentDirectory))
  val function = InversFunction(process, don, experimentData)
}
