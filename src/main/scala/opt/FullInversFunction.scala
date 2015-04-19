package opt

import akka.actor.ActorRef
import ui.Protocol.Iteration
import ui.view.InversView

case class FullInversFunction(args: Seq[InversView],
    progress: ActorRef) {

  val functions = args.map(HSFunction)

  private val strains = (0 to 100).map(_ / 100d)

  def fitness(args: Seq[Double]): Double = {
    val Seq(a1, m1, m2, m3, m4, m5, m6, m7, m8, m9, _) = args
    val fitness = functions.map { function =>
      strains.map(strain => function.fitness(a1, m1, m2, m3, m4, m5, m6, m7, m8, m9, strain)).sum
    }.sum

    progress ! Iteration(fitness, System.nanoTime())
    fitness
  }

}
