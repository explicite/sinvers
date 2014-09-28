package io

import data.Data
import regex.Parser

import scala.concurrent.ExecutionContext
import scala.sys.process._

case class Forge(fx2Dir: String) extends Parser {
  private val JOB = getClass.getResource("/job.bat").getPath

  def process(don: DON)(implicit ec: ExecutionContext): Data = {
    val donName = don.name
    val command = Seq(JOB, fx2Dir, donName, don.workingDirectory)

    val data = run(command)(don)
    val (load, height) = data.unzip

    Data(load, height)
  }


  def run(command: Seq[String])(don: DON): Seq[(Double, Double)] = {
    try {
      don.refresh()
      find(None, None)(command.lineStream)(Seq.empty)
    } catch {
      case e: Exception =>
        Seq.empty
    }
  }

  private def find(load: Option[Double] = None, height: Option[Double] = None)
                  (stream: Stream[String])
                  (seq: Seq[(Double, Double)] = Seq.empty): Seq[(Double, Double)] = {
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
}