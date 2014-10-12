package data

import java.io.File

import math.PolynomialSplineFunction
import opt.Interval

import scala.io.Source

case class Data(force: Seq[Double],
                jaw: Seq[Double]) {
  private val KGF = 1016.0469053138122
  val size = {
    if (force.size != jaw.size) throw new Exception("Data length for force and displacement are not equal!")
    force.size
  }
  val nonEmpty = size != 0

  def valid(interval: Interval): Boolean = nonEmpty && size >= 30

  def fit(experimentDataInterpolator: PolynomialSplineFunction, interval: Interval): Double = {
    if (valid(interval)) {
      val computedForce = force.map(_ * KGF)
      val interpolatedForce = jaw.map(experimentDataInterpolator(_))
      computedForce.zip(interpolatedForce).map { case (c, ii) => scala.math.sqrt((c - ii) * (c - ii))}.sum / computedForce.size
    } else {
      Double.MaxValue
    }
  }
}

object Data {
  def apply(file: File): Data = {
    val source = Source.fromFile(file)

    val lines = source.nonEmpty match {
      case true =>
        source.getLines().drop(1)
      case false => throw new Exception(s"Empty data file:${file.getName}")
    }

    val (force, jaw) = {
      for (line <- lines) yield {
        line.split("\\s+") match {
          case Array(_, f, j, _, _, _, _, _, _) => (f.toDouble, j.toDouble)
          case _ => throw new Exception(s"Inconsistent date in file:${file.getName}")
        }
      }
    }.toSeq.unzip

    Data(force, jaw)
  }

  val empty = Data(Seq.empty, Seq.empty)

  implicit def ToupleToData(sx: Seq[(Double, Double)]): Data = {
    val (force, jaw) = sx.unzip
    Data(force, jaw)
  }
}

case class DataFile(file: File) {
  def current = Data(file)
}
