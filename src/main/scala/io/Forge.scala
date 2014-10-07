package io

import java.io.InputStream
import java.nio.file.Paths

import data.Data
import regex.Parser
import util.FileManipulator

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.sys.process._

case class Forge(fx2Dir: String) extends Parser {

  def process(don: DON): Data = {
    val process: ProcessBuilder =
      Process(
        Seq(s"$fx2Dir\\bin\\xf2_p1.exe", don.name),
        don.file.getParentFile,
        "PP2D_DIR" -> fx2Dir,
        "FORGE2_IO" -> "BIG_ENDIAN",
        "lang" -> "eng",
        "WORK_DIR" -> "don.workingDirectory")

    compute(process)(don)
  }


  def fallback[F](function: => F)(to: Duration, default: F, onTheEnd: () => Unit): F = {
    try {
      val future = Future {
        function
      }
      Await.result(future, to)
    } catch {
      case t: Throwable => println(t.getMessage); default
    } finally {
      onTheEnd()
    }
  }

  def compute(process: ProcessBuilder)(don: DON): Data = {
    don.refresh()

    val random = new util.XORShiftRandom()
    var load = List[Double]()
    var height = List[Double]()
    var current: Process = null
    def processDataLine(line: String): Unit = {
      VirtualLoadRegex findFirstIn line match {
        case Some(VirtualLoadRegex(_, mantissa, exponent)) => load ::= formatDouble(mantissa, exponent)
        case None => Unit
      }
      HeightRegex findFirstIn line match {
        case Some(HeightRegex(_, mantissa, exponent)) => height ::= formatDouble(mantissa, exponent)
        case None => Unit
      }
    }
    def processData(inputStream: InputStream): Unit = {
      try {
        scala.io.Source.fromInputStream(inputStream).getLines().foreach(processDataLine)
      } finally {
        inputStream.close()
      }
    }

    def processErrorLine(line: String): Unit = {
      current.destroy()
    }

    def processError(inputStream: InputStream): Unit = {
      try {
        scala.io.Source.fromInputStream(inputStream).getLines().foreach(processErrorLine)
      } finally {
        inputStream.close()
      }
    }


    def end() = {
      println(s"${don.file.getParentFile.getName} on the end")
      if (current != null)
        current.destroy()

      cleanUp(don.file.getParentFile)
    }

    val io = new ProcessIO(
      in => (),
      processData,
      processError
    )

    Thread.sleep(random.nextDouble(0, 2000).toLong)

    fallback {
      current = process.run(io)
      current.exitValue()
      Data(load, height)
    }(10 minutes, Data.empty, end)
  }


  private def find(load: Option[Double] = None, height: Option[Double] = None)
                  (stream: Stream[String])
                  (seq: Seq[(Double, Double)] = Nil): Seq[(Double, Double)] = {
    stream match {
      case head #:: tail =>
        (load, height) match {
          case (None, None) => VirtualLoadRegex findFirstIn head match {
            case Some(VirtualLoadRegex(_, mantissa, exponent)) =>
              find(Some(formatDouble(mantissa, exponent)), None)(tail)(seq)
            case _ => find(None, None)(tail)(seq)
          }
          case (Some(l), None) => HeightRegex findFirstIn head match {
            case Some(HeightRegex(_, mantissa, exponent)) =>
              find(None, None)(tail)(seq :+(l, formatDouble(mantissa, exponent)))
            case _ => find(Some(l), None)(tail)(seq)
          }
          case (None, Some(h)) => VirtualLoadRegex findFirstIn head match {
            case Some(VirtualLoadRegex(_, mantissa, exponent)) =>
              find(None, None)(tail)(seq :+(formatDouble(mantissa, exponent), h))
            case _ => find(None, Some(h))(tail)(seq)
          }
          case (Some(l), Some(h)) => find(None, None)(tail)(seq :+(l, h))
        }
      case _ => seq
    }
  }

  private def cleanUp(file: java.io.File): Unit = {
    try {
      println(s"${file.getName} on clean")
      if (file.exists()) {
        val fileManipulator = new FileManipulator()
        fileManipulator.DeleteDirectory(Paths.get(file.getPath))
      }
    } catch {
      case t: Throwable => ()
    }
  }
}