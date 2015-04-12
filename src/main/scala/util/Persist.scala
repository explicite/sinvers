package util

import data.DataContainer

object Persist {
  def in(f: java.io.File)(op: java.io.PrintWriter => Unit) = {
    val p = new java.io.PrintWriter(f)
    try op(p) finally p.close()
  }

  def data(data: DataContainer, file: java.io.File) = in(file) { printWriter =>
    val toPrint = (data.time zip data.force zip data.jaw).map {
      case ((t, f), j) => (t, f, j)
    }.reverseMap { case (t, f, j) => s"$t $f $j" }
    toPrint.foreach(printWriter.println)
  }

  def zipped(toupled: Seq[(Double, Double)], file: java.io.File) = in(file) { printWriter =>
    toupled.foreach {
      case (a, b) => printWriter.println(s"$a $b")
    }
  }

}
