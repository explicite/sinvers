package test

import akka.actor.ActorSystem
import org.scalatest.{ FlatSpecLike, Matchers }

trait BaseTest extends FlatSpecLike with Matchers {
  val system = ActorSystem("sinvers-test")
}
