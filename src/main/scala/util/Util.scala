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
      fn
    } match {
      case x: Success[T] => x
      case _ if n > 1 => retry(n - 1)(fn)
      case f => f
    }
  }

  def copy(source: Path, target: Path): Path = {
    Util.retry(5) {
      Files.copy(source, target, REPLACE_EXISTING)
    } match {
      case Success(path) => path
      case Failure(err) => throw err
    }
  }

  def delete(target: Path): Path = {
    Util.retry(5) {
      FileManipulator.DeleteDirectory(target)
    } match {
      case Success(path) => path
      case Failure(err) => throw err
    }
  }

  def write(target: Path, data: Array[Byte]): Path = {
    Try {
      val out = new BufferedOutputStream(Files.newOutputStream(target, CREATE, TRUNCATE_EXISTING))
      out.write(data, 0, data.length)
    } match {
      case Success(res) => target
      case Failure(err) => throw err
    }
  }
}
