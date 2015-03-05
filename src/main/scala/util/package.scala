import scala.util.{Success, Try}

package object util {
  def retry[T](n: Int)(fn: => T): Try[T] = {
    Try { fn } match {
      case x: Success[T] => x
      case _ if n > 1 => retry(n - 1)(fn)
      case f => f
    }
  }
}
