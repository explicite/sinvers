package data

import java.io.File
import java.nio.file.{ Files, Path }

import math.{ Interpolator, PolynomialSplineFunction }
import opt.Interval
import util.KZ
import util.Util.{ scienceFormatter, trimmedFormatter, write }

import scala.io.Source
import scala.language.implicitConversions
import scala.util.{ Failure, Success, Try }

case class DataContainer(time: Seq[Double],
    force: Seq[Double],
    jaw: Seq[Double]) extends Container {

  type T = DataContainer

  def steering(maxJaw: Double): Path = {
    val file = Files.createTempFile("sterring", "dat")
    write(file, steeringData(maxJaw))
  }

  private def steeringData(maxJaw: Double): Array[Byte] = {
    time.map(_ - jaw.head).zip(maxJaw +: jaw.map(_ + maxJaw)).map {
      case (t, j) => s"${trimmedFormatter(t)}, ${trimmedFormatter(j)}"
    }.mkString("\n").getBytes
  }

  def toPrint: Seq[String] = {
    val result = (time zip force zip jaw).map {
      case ((t, f), j) => (t, f, j)
    }.reverseMap {
      case (t, f, j) =>
        s"${scienceFormatter(t)}\t${scienceFormatter(f)}\t${scienceFormatter(j)}"
    }
    "Time(sec)\tForce(kgf)\tJaw(mm)" +: result
  }

  def slice(interval: Interval): DataContainer = {
    (time zip force zip jaw).map {
      case ((t, f), j) => (t, f, j)
    }.filter {
      case (t, f, j) =>
        j >= interval.min && j <= interval.max
    }
  }

  def filter(k: Double, m: Int): DataContainer = {
    copy(force = KZ(force, k, m))
  }

  def interpolator(conversion: Force, maxJaw: Double): PolynomialSplineFunction = {
    val (filteredForce, filteredJaw) = force.zip(jaw).groupBy(_._2).map(_._2.head).toSeq.sortBy(_._2).unzip
    val convertedForce = filteredForce.map(conversion.toTones)
    val convertedJaw = filteredJaw.map(_ + maxJaw)
    Interpolator.splineInterpolate(convertedJaw.toArray, convertedForce.toArray)
  }

}

object DataContainer {

  implicit def ToupleToDataContainer(sx: Seq[(Double, Double, Double)]): DataContainer = {
    val (time, force, jaw) = sx.toList.unzip3
    DataContainer(time, force, jaw)
  }

  def apply(file: File): DataContainer = {
    val source = Source.fromFile(file)

    val lines = source.nonEmpty match {
      case true => source.getLines().dropWhile {
        line =>
          Try(line.split("\\s+").head.toDouble) match {
            case Success(v) => false
            case Failure(e) => true
          }
      }
      case false => throw new Exception(s"Empty data file:${file.getName}")
    }

    val (time, force, jaw) = {
      for (line <- lines) yield {
        line.split("\\s+") match {
          case array if array.length >= 3 =>
            (array(0).toDouble, array(1).toDouble, array(2).toDouble)
          case _ =>
            throw new Exception(s"Inconsistent date in file:${file.getName}")
        }
      }
    }.toList.unzip3

    DataContainer(time, force, jaw)
  }

}
