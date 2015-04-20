package util

import java.io.BufferedOutputStream
import java.nio.file.StandardCopyOption._
import java.nio.file.StandardOpenOption._
import java.nio.file.{ Files, Path }

import scala.util.{ Failure, Success, Try }

object Util {
  val FileManipulator = new FileManipulator

  def retry[T](n: Int)(fn: => T): Try[T] = {
    Try {
      Thread.sleep(100 * (11 - n))
      fn
    } match {
      case x: Success[T] => x
      case _ if n > 1    => retry(n - 1)(fn)
      case f             => f
    }
  }

  def copy(source: Path, target: Path): Path = {
    Util.retry(10) {
      Files.copy(source, target, REPLACE_EXISTING)
    } match {
      case Success(path) => path
      case Failure(err)  => throw err
    }
  }

  def delete(target: Path): Path = {
    Util.retry(10) {
      if (!Files.notExists(target))
        FileManipulator.DeleteDirectory(target)
      target
    } match {
      case Success(path) => path
      case Failure(err)  => target
    }
  }

  def write(target: Path, data: Array[Byte]): Path = {
    val out = new BufferedOutputStream(Files.newOutputStream(target, CREATE, TRUNCATE_EXISTING))
    try {
      out.write(data, 0, data.length)
      target
    } finally {
      out.flush()
      out.close()
    }
  }

  def time[T](block: => T): T = {
    val t0 = System.nanoTime()
    val result = block // call-by-name
    val t1 = System.nanoTime()
    result
  }

  def scienceFormatter(d: Double): String = new java.text.DecimalFormat("0.##############E0").format(d)
  def scienceLowFormatter(d: Double): String = new java.text.DecimalFormat("0.###E0").format(d)
  def trimmedFormatter(d: Double): String = new java.text.DecimalFormat("0.#####").format(d)
}
