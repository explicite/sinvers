package io

import akka.actor.{ Actor, ActorLogging, Props }
import data.KGF
import io.Protocol.Optimize
import opt.{ FitnessFunction, GreyWolfOptimizer }
import reo.HSArgs
import ui.controls.Progress

class Simulation extends Actor with ActorLogging {
  val progress = context.actorOf(Props[Progress])

  override def receive: Receive = {
    case Optimize(forge, simulation, experiment, temperature, strainRate, bounds, wolfs, iterations) =>
      val function = FitnessFunction(forge, simulation, temperature, context.system, experiment, KGF, progress)

      def fit(sx: Seq[Double]): Double = {
        function.fitness(sx)
      }

      val optimizer = GreyWolfOptimizer(fit, bounds)

      //create new progress bar for minimize
      progress ! ui.controls.ProgressProtocol.SetStart(System.nanoTime(), wolfs * iterations)
      val min = optimizer.min(wolfs, iterations)
      progress ! ui.controls.ProgressProtocol.SetEnd(temperature, strainRate, HSArgs(min))
  }
}
