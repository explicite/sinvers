package io

import java.nio.file.Paths
import java.util.concurrent.Executors

import data.Data
import regex.Parser
import util.{ FileManipulator, Util }

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.sys.process._
import scala.util.{ Failure, Success, Try }

case class Forge(xf2Dir: String) extends Parser {
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(4))

  def process(don: DON): Data = {
    val process: ProcessBuilder =
      Process(
        Seq(s"$xf2Dir\\bin\\xf2_p1.exe", don.name),
        don.file.getParentFile,
        "PP2D_DIR" -> xf2Dir,
        "FORGE2_IO" -> "BIG_ENDIAN",
        "lang" -> "eng",
        "WORK_DIR" -> "don.workingDirectory")

    compute(process)(don)
  }

  def fallback[F](function: => F)(to: Duration, default: F, onTheEnd: () => Unit): F = {
    val result = Try(Await.result(Future {
      function
    }, to)) match {
      case Success(x) => x
      case Failure(_) => default
    }
    onTheEnd()
    result
  }

  def compute(process: ProcessBuilder)(don: DON): Data = {
    don.refresh()
    var time = List[Double]()
    var load = List[Double]()
    var height = List[Double]()
    var velocity = List[Double]()
    var current: Process = null
    def processDataLine(line: String): Unit = {
      IncrementTimeRegex findFirstIn line match {
        case Some(IncrementTimeRegex(_, mantissa, exponent)) => time ::= formatDouble(mantissa, exponent)
        case None => Unit
      }
      VirtualLoadRegex findFirstIn line match {
        case Some(VirtualLoadRegex(_, mantissa, exponent)) => load ::= formatDouble(mantissa, exponent)
        case None => Unit
      }
      HeightRegex findFirstIn line match {
        case Some(HeightRegex(_, mantissa, exponent)) => height ::= formatDouble(mantissa, exponent)
        case None                                     => Unit
      }
      VelocityRegex findFirstIn line match {
        case Some(VelocityRegex(_, mantissa, exponent)) => velocity ::= formatDouble(mantissa, exponent)
        case None                                       => Unit
      }
    }

    def onTheEnd() = {
      if (current != null)
        current.destroy()
      cleanUp(don.file.getParentFile)
    }

    val io = new ProcessIO(
      in => (),
      data => try {
        scala.io.Source.fromInputStream(data).getLines().foreach(processDataLine)
      } finally {
        data.close()
      },
      error => error.close()
    )

    //Thread.sleep(random.nextDouble(0, 2000).toLong)

    fallback {
      current = process.run(io)
      current.exitValue()
      Data(time, load, height, velocity)
    }(20 seconds, Data.empty, onTheEnd)
  }

  private def cleanUp(file: java.io.File): Unit = {
    Util.retry(5) {
      if (file.exists()) {
        Thread.sleep(2000)
        val fileManipulator = new FileManipulator()
        fileManipulator.DeleteDirectory(Paths.get(file.getPath))
      }
    }
  }

}