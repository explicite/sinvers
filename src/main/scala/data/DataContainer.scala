package data

import java.io.File
import java.nio.file.{ Files, Path }

import math.{ Interpolator, PolynomialSplineFunction }
import opt.Interval
import util.KZ
import util.Util.{ scienceFormatter, trimmedFormatter, write }

import scala.io.Source
import scala.language.implicitConversions

case class DataContainer(time: Seq[Double],
    force: Seq[Double],
    jaw: Seq[Double],
    ptemp: Seq[Double],
    strain: Seq[Double],
    stress: Seq[Double],
    stroke: Seq[Double],
    tc1: Seq[Double],
    tc2: Seq[Double]) extends Container {

  type T = DataContainer

  def steering(maxJaw: Double): Path = {
    val file = Files.createTempFile("sterring", "dat")
    write(file, steeringData(maxJaw))
  }

  private def steeringData(maxJaw: Double): Array[Byte] = {
    val droppedTime = time.drop(2984)
    val droppedJaw = jaw.drop(2984)
    droppedTime.map(_ - droppedTime.head).zip(maxJaw +: droppedJaw.map(_ + maxJaw)).map {
      case (t, j) => s"${trimmedFormatter(t)}, ${trimmedFormatter(j)}"
    }.mkString("\n").getBytes
  }

  def toPrint: Seq[String] = {
    val result = (time zip force zip jaw zip ptemp zip strain zip stress zip stroke zip tc1 zip tc2).map {
      case ((((((((t, f), j), p), stn), sts), ste), t1), t2) => (t, f, j, p, stn, sts, ste, t1, t2)
    }.reverseMap {
      case (t, f, j, p, stn, sts, ste, t1, t2) =>
        s"${scienceFormatter(t)}\t${scienceFormatter(f)}\t${scienceFormatter(j)}\t${scienceFormatter(p)}\t${scienceFormatter(stn)}\t${scienceFormatter(sts)}\t${scienceFormatter(ste)}\t${scienceFormatter(t1)}\t${scienceFormatter(t2)}"
    }
    "Time(sec)\tForce(kgf)\tJaw(mm)\tPtemp(C)\tStrain\tStress(MPa)\tStroke(mm)\tTC1(C)\tTC2(C)" +: result
  }

  def slice(interval: Interval): DataContainer = {
    (time zip force zip jaw zip ptemp zip strain zip stress zip stroke zip tc1 zip tc2).map {
      case ((((((((t, f), j), p), stn), sts), ste), t1), t2) => (t, f, j, p, stn, sts, ste, t1, t2)
    }.filter {
      case (t, f, j, p, stn, sts, ste, t1, t2) =>
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

  implicit class Unzip9[A, B, C, D, E, F, G, H, I](val xs: List[(A, B, C, D, E, F, G, H, I)]) extends AnyVal {
    def unzip9: (List[A], List[B], List[C], List[D], List[E], List[F], List[G], List[H], List[I]) =
      xs.foldRight[(List[A], List[B], List[C], List[D], List[E], List[F], List[G], List[H], List[I])]((Nil, Nil, Nil, Nil, Nil, Nil, Nil, Nil, Nil)) {
        (x, res) =>
          val (a, b, c, d, e, f, g, h, i) = x
          (a :: res._1, b :: res._2, c :: res._3, d :: res._4, e :: res._5, f :: res._6, g :: res._7, h :: res._8, i :: res._9)
      }
  }

  implicit def ToupleToDataContainer(sx: Seq[(Double, Double, Double, Double, Double, Double, Double, Double, Double)]): DataContainer = {
    val (time, force, jaw, ptemp, strain, stress, stroke, tc1, tc2) = sx.toList.unzip9
    DataContainer(time, force, jaw, ptemp, strain, stress, stroke, tc1, tc2)
  }

  def apply(file: File): DataContainer = {
    val source = Source.fromFile(file)

    val lines = source.nonEmpty match {
      case true  => source.getLines().drop(1)
      case false => throw new Exception(s"Empty data file:${file.getName}")
    }

    val (time, force, jaw, ptemp, strain, stress, stroke, tc1, tc2) = {
      for (line <- lines) yield {
        line.split("\\s+") match {
          case Array(t, f, j, p, stn, sts, ste, t1, t2) => (t.toDouble, f.toDouble, j.toDouble, p.toDouble, stn.toDouble, sts.toDouble, ste.toDouble, t1.toDouble, t2.toDouble)
          case _                                        => throw new Exception(s"Inconsistent date in file:${file.getName}")
        }
      }
    }.toList.unzip9

    DataContainer(time, force, jaw, ptemp, strain, stress, stroke, tc1, tc2)
  }

}
