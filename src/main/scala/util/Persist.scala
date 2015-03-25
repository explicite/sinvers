package util

import data.DataContainer

object Persist {
  def in(f: java.io.File)(op: java.io.PrintWriter => Unit) = {
    val p = new java.io.PrintWriter(f)
    try op(p) finally p.close()
  }

  def data(data: DataContainer, file: java.io.File) = in(file) { printWriter =>
    val toPrint = (data.time zip data.force zip data.jaw zip data.velocity).map {
      case (((t, f), j), v) => (t, f, j, v)
    }.reverse.map { case (t, f, j, v) => s"$v $f $j $t 0.0 0.0 0.0 0.0 0.0" }
    toPrint.foreach(printWriter.println)
  }

  def zipped(toupled: Seq[(Double, Double)], file: java.io.File) = in(file) { printWriter =>
    toupled.foreach {
      case (a, b) => printWriter.println(s"$a $b")
    }
  }

}
