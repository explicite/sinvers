package opt

import akka.actor.ActorRef
import ui.Protocol.Iteration
import ui.view.InversView

case class FullInversFunction(args: Seq[InversView],
    progress: ActorRef) {

  val functions = args.map(HSFunction)

  private val strains = (1 to 100).map(_ / 100d)

  def fitness(args: Seq[Double]): Double = {
    val fitness = functions.map { function =>
      val result = strains.map(strain => function.fitness(args, strain)).sum / strains.size
      result - scala.math.E
    }.sum

    progress ! Iteration(fitness, System.nanoTime())
    fitness
  }

}
