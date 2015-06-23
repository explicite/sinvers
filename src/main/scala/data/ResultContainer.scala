package data

import math.PolynomialSplineFunction
import opt.Interval
import util.KZ
import util.Util.scienceFormatter

import scala.language.implicitConversions

case class ResultContainer(time: Seq[Double],
    force: Seq[Double],
    jaw: Seq[Double],
    velocity: Seq[Double]) extends Container {

  type T = ResultContainer

  def score(interpolator: PolynomialSplineFunction, interval: Interval): Double = {
    val slice: ResultContainer = (time zip force zip jaw zip velocity).map {
      case (((t, f), j), v) => (t, f, j, v)
    }.filter {
      case (t, f, j, v) =>
        j >= interval.min && j <= interval.max
    }

    if (slice.force.size >= 20) {
      val (forceToInter, jawToInter) = sections(slice.force.zip(slice.jaw), 10).unzip
      val interpolatedForce = jawToInter.map(interpolator.apply)
      val result = forceToInter.zip(interpolatedForce).map {
        case (c, ii) => (c - ii) * (c - ii)
      }.sum

      math.sqrt(result / forceToInter.size.toDouble).toDouble
    } else {
      Double.MaxValue
    }
  }

  def toPrint: Seq[String] = {
    val result = (time zip force zip jaw zip velocity).map {
      case (((t, f), j), v) => (t, f, j, v)
    }.reverseMap { case (t, f, j, v) => s"${scienceFormatter(v)}\t${scienceFormatter(f)}\t${scienceFormatter(j)}\t${scienceFormatter(v)}" }
    "Time(sec)\tForce(kgf)\tJaw(mm)\tvelocity(mm/s)" +: result
  }

  def slice(interval: Interval): ResultContainer = {
    (time zip force zip jaw zip velocity).map {
      case (((t, f), j), v) => (t, f, j, v)
    }.filter {
      case (t, f, j, v) =>
        j >= interval.min && j <= interval.max
    }
  }

  def filter(k: Double, m: Int): ResultContainer = {
    copy(force = KZ(force, k, m))
  }

  private def sections(sx: Seq[(Double, Double)], slices: Int): List[(Double, Double)] = {
    val (_, jaw) = sx.unzip
    val min = jaw.min
    val max = jaw.max
    val span = (max - min) / slices
    val splits = List.iterate(min, slices)(_ + span) :+ max
    splits.flatMap { s => sx.find { case (f, j) => j < s + span && j > s - span } }.distinct
  }

}

object ResultContainer {

  implicit class Unzip4[A, B, C, D](val xs: List[(A, B, C, D)]) extends AnyVal {
    def unzip4: (List[A], List[B], List[C], List[D]) =
      xs.foldRight[(List[A], List[B], List[C], List[D])]((Nil, Nil, Nil, Nil)) {
        (x, res) =>
          val (a, b, c, d) = x
          (a :: res._1, b :: res._2, c :: res._3, d :: res._4)
      }
  }

  implicit def ToupleToResultContainer(sx: Seq[(Double, Double, Double, Double)]): ResultContainer = {
    val (time, force, jaw, velocity) = sx.toList.unzip4
    ResultContainer(time, force, jaw, velocity)
  }

  val empty = ResultContainer(Nil, Nil, Nil, Nil)

}