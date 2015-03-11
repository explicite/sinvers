package util

import java.nio.file.StandardCopyOption._
import java.nio.file.{ Files, Path }

import scala.util.{ Success, Try }

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
    }
  }

  def delete(target: Path): Path = {
    Util.retry(5) {
      FileManipulator.DeleteDirectory(target)
    } match {
      case Success(path) => path
    }
  }
}
