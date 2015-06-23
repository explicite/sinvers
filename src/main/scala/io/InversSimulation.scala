package io

import akka.actor.{ Actor, ActorLogging, Props }
import data.KGF
import io.Protocol.OptimizeInvers
import opt.InversFunction.SetData
import opt.{ StaticInterval, FunctionContainer, InversFunction, GreyWolfOptimizer }
import ui.controls.InversProgress

class InversSimulation extends Actor with ActorLogging {
  val progress = context.actorOf(Props[InversProgress])

  override def receive: Receive = toSet

  def toSet: Receive = {
    case OptimizeInvers(forge, simulation, experiment, temperature, strainRate, bounds, wolfs, iterations) =>
      val inversFunction = context.actorOf(Props[InversFunction])
      inversFunction ! SetData(forge, simulation, temperature, experiment, KGF)
      context become set(temperature, strainRate, wolfs, iterations, bounds)
  }

  def set(temperature: Double, strainRate: Double, wolfs: Int, iterations: Int, bounds: Seq[StaticInterval]): Receive = {
    case container: FunctionContainer =>
      val optimizer = GreyWolfOptimizer(container.function(progress), bounds)

      //create new progress bar for minimize
      progress ! ui.controls.ProgressProtocol.SetStart(System.nanoTime(), wolfs * iterations)
      val min = optimizer.min(wolfs, iterations)
      progress ! ui.controls.ProgressProtocol.SetEnd(temperature, strainRate, min)
  }
}
