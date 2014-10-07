package data

import java.io.File

import scala.io.Source

case class Data(force: Seq[Double],
                jaw: Seq[Double]) {
  val isEmpty = force.size == 0 && jaw.size == 0
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
