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

  /*private def steeringData(startJaw: Double): Array[Byte] = {
    val (correctJaw, correctTime) = des(jaw.map(_ + startJaw).zip(time.map(_ - time.min)).reverse).unzip
    val inter = Interpolator.splineInterpolate(correctJaw.toArray, correctTime.toArray)
    val jawStamp = 0.002
    val nJaws = (startJaw / jawStamp).toInt
    val jawSx = (0 to nJaws).map(j => BigDecimal(j) * jawStamp).filter(d => d > correctJaw.min + jawStamp && d < correctJaw.max - jawStamp)
    val timeSx = jawSx.map(j => inter.apply(j.toDouble))
    val filteredData = (timeSx :+ BigDecimal(0)).zip(jawSx :+ BigDecimal(startJaw)).reverse.toList
    filteredData.map {
      case (t, j) => s"${trimmedFormatter(t.toDouble)}, ${trimmedFormatter(j.toDouble)}"
    }.mkString("\n").getBytes
  }*/

  private def steeringData(startJaw: Double): Array[Byte] = {
    val filteredData = jawSection(des(jaw.map(_ + startJaw).zip(time.map(_ - time.min))), 2500).reverse
    filteredData.map {
      case (t, j) => s"${trimmedFormatter(t.toDouble)}, ${trimmedFormatter(j.toDouble)}"
    }.mkString("\n").getBytes
  }

  private def des(sx: Seq[(Double, Double)]): Seq[(Double, Double)] = {
    sx.foldLeft(List(sx.head)) {
      case (d, (t, j)) =>
        val (_, lastJaw) = d.last
        if (lastJaw <= j) d :+ (t, j) else d
    }
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
    val (nt, nf, nj) = (time zip force zip jaw).map {
      case ((t, f), j) => (t, f, j)
    }.filter {
      case (t, f, j) =>
        j >= interval.min && j <= interval.max
    }.unzip3
    DataContainer(nt, nf, nj)
  }

  private def jawSection(sx: Seq[(Double, Double)], slices: Int): List[(Double, Double)] = {
    val (jaw, _) = sx.unzip
    val min = jaw.min
    val max = jaw.max
    val span = (max - min) / slices
    val splits = List.iterate(jaw.min, slices)(_ + span)
    splits.flatMap { s =>
      sx.filter {
        case (j, t) => j <= s + span && j >= s - span
      }.sortBy(_._2).headOption
    }.distinct
  }

  def filter(m: Double, k: Int): DataContainer = {
    copy(force = KZ(force, m, k))
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
