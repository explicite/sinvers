package test

import akka.actor.{ Props, ActorSystem }
import org.scalatest.{ FlatSpecLike, Matchers }
import ui.controls.FitnessChart

trait BaseTest extends FlatSpecLike with Matchers {
  val system = ActorSystem("sinvers-test")
  val fitnessChart = system.actorOf(Props[FitnessChart].withDispatcher("scalafx-dispatcher"), "fitness-chart")
}
