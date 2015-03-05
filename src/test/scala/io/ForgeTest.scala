package io

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Minutes, Seconds, Span }
import test.BaseTest

import scala.concurrent.ExecutionContext.Implicits.global

class ForgeTest extends BaseTest with ScalaFutures {
  val fx2Dir = "C:\\Users\\Jan\\Desktop\\Forge2-V3.0"
  val workingDirectory = "C:\\Users\\Jan\\Desktop\\test"
  val don = DON(new java.io.File(s"$workingDirectory\\file.don"))
  val forge = Forge(fx2Dir)

  behavior of "Forge process"
  implicit val defaultPatience =
    PatienceConfig(timeout = Span(20, Minutes), interval = Span(1, Seconds))

  /*
  it should "run forge process" in whenReady(forge process don) { result =>
    result.force should not be empty
  }
  */
}
