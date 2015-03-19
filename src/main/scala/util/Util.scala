package util

import java.io.BufferedOutputStream
import java.nio.file.StandardCopyOption._
import java.nio.file.StandardOpenOption._
import java.nio.file.{ Files, Path }
import java.time.Duration

import scala.util.{ Failure, Success, Try }

object Util {
  val FileManipulator = new FileManipulator

  def retry[T](n: Int)(fn: => T): Try[T] = {
    Try {
      fn
    } match {
      case x: Success[T] => x
      case _ if n > 1    => retry(n - 1)(fn)
      case f             => f
    }
  }

  def copy(source: Path, target: Path): Path = {
    Util.retry(5) {
      Files.copy(source, target, REPLACE_EXISTING)
    } match {
      case Success(path) => path
      case Failure(err)  => throw err
    }
  }

  def delete(target: Path): Path = {
    Util.retry(5) {
      Thread.sleep(200)
      if (!Files.notExists(target))
        FileManipulator.DeleteDirectory(target)
      target
    } match {
      case Success(path) => path
      case Failure(err)  => throw err
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
    println(s"elapsed time: ${Duration.ofNanos(t1 - t0)}")
    result
  }

  def formatter(d: Double): String = new java.text.DecimalFormat("0.##############E0").format(d)
}
