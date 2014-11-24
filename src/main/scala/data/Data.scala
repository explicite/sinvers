package data

import java.io.File

import math.PolynomialSplineFunction
import opt.Interval

import scala.io.Source

case class Data(time: Seq[Double],
                force: Seq[Double],
                jaw: Seq[Double],
                velocity: Seq[Double]) {
  private val KGF = 1016.0469053138122
  val size = {
    if (force.size != jaw.size) throw new Exception("Data length for force and displacement are not equal!")
    force.size
  }
  val nonEmpty = size != 0

  def valid(interval: Interval): Boolean = nonEmpty && size >= 30

  def fit(interpolator: PolynomialSplineFunction, interval: Interval): Double = {
    if (valid(interval)) {
      val computedForce = force //.map(_ * KGF)
      val interpolatedForce = jaw.map(interpolator(_))
      computedForce.zip(interpolatedForce).map { case (c, ii) => scala.math.sqrt((c - ii) * (c - ii))}.sum / computedForce.size
    } else {
      Double.MaxValue
    }
  }

  def save(file: File): Unit = {
    def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
      val p = new java.io.PrintWriter(f)
      try {
        op(p)
      } finally {
        p.close()
      }
    }

    printToFile(file) {
      printWriter =>
        val toPrint = ((time zip force zip jaw zip velocity) map { case (((t, f), j), v) => (t, f, j, v)}).reverse.map {
          case (t, f, j, v) =>
            s"$t $f $j $v 0.0 0.0 0.0 0.0 0.0"
        }
        toPrint.foreach(printWriter.println)
    }
  }

}

object Data {

  implicit class Unzip4[A, B, C, D](val xs: List[(A, B, C, D)]) extends AnyVal {
    def unzip4: (List[A], List[B], List[C], List[D]) = xs.foldRight[(List[A], List[B], List[C], List[D])]((Nil, Nil, Nil, Nil)) { (x, res) =>
      val (a, b, c, d) = x
      (a :: res._1, b :: res._2, c :: res._3, d :: res._4)
    }
  }

  def apply(file: File): Data = {
    val source = Source.fromFile(file)

    val lines = source.nonEmpty match {
      case true =>
        source.getLines().drop(1)
      case false => throw new Exception(s"Empty data file:${file.getName}")
    }

    val (time, force, jaw, velocity) = {
      for (line <- lines) yield {
        line.split("\\s+") match {
          case Array(t, f, j, v, _, _, _, _, _) => (t.toDouble, f.toDouble, j.toDouble, v.toDouble)
          case _ => throw new Exception(s"Inconsistent date in file:${file.getName}")
        }
      }
    }.toList.unzip4

    Data(time, force, jaw, velocity)
  }

  val empty = Data(Seq.empty, Seq.empty, Seq.empty, Seq.empty)

  implicit def ToupleToData(sx: Seq[(Double, Double, Double, Double)]): Data = {
    val (time, force, jaw, velocity) = sx.toList.unzip4
    Data(time, force, jaw, velocity)
  }
}

case class DataFile(file: File) {
  def current = Data(file)
}


