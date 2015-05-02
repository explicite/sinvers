package io

import akka.actor.{ Actor, ActorLogging, Props }
import io.Protocol.OptimizeFullInvers
import opt.{ FullInversFunction, GreyWolfOptimizer }
import ui.controls.FullInversProgress

class FullInversSimulation extends Actor with ActorLogging {
  val progress = context.actorOf(Props[FullInversProgress])

  override def receive: Receive = {
    case OptimizeFullInvers(inversViews, bounds, wolfs, iterations) =>
      val function = FullInversFunction(inversViews, progress)
      val optimizer = GreyWolfOptimizer(function.fitness, bounds)

      //create new progress bar for minimize
      progress ! ui.controls.ProgressProtocol.SetStart(System.nanoTime(), wolfs * iterations)
      val min = optimizer.min(wolfs, iterations)
      progress ! ui.controls.FullInversProgress.SetEnd(inversViews.map(_.id), min)
  }

}

