package io

import akka.actor.{ Actor, ActorLogging, Props }
import data.KGF
import io.Protocol.OptimizeInvers
import opt.{ InversFunction, GreyWolfOptimizer }
import reo.HSArgs
import ui.controls.InversProgress

class InversSimulation extends Actor with ActorLogging {
  val progress = context.actorOf(Props[InversProgress])

  override def receive: Receive = {
    case OptimizeInvers(forge, simulation, experiment, temperature, strainRate, bounds, wolfs, iterations) =>
      val function = InversFunction(forge, simulation, temperature, context.system, experiment, KGF, progress)
      val optimizer = GreyWolfOptimizer(function.fitness, bounds)

      //create new progress bar for minimize
      progress ! ui.controls.ProgressProtocol.SetStart(System.nanoTime(), wolfs * iterations)
      val min = optimizer.min(wolfs, iterations)
      progress ! ui.controls.ProgressProtocol.SetEnd(temperature, strainRate, HSArgs(min))
  }
}
