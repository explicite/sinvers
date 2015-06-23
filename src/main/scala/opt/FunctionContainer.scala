package opt

import akka.actor.ActorRef
import ui.Protocol.Iteration

case class FunctionContainer(function: Seq[Double] => Double) {
  def function(progress: ActorRef)(sx: Seq[Double]): Double = {
    val fitness = function(sx)
    progress ! Iteration(fitness, System.nanoTime())
    fitness
  }
}
