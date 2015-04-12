package io

import akka.actor.{ Actor, ActorLogging, Props }
import data.KGF
import io.Protocol.Optimize
import opt.{ FitnessFunction, GreyWolfOptimizer, StaticInterval }
import reo.HSArgs
import ui.controls.Progress
import util.Util

class Simulation extends Actor with ActorLogging {
  val progress = context.actorOf(Props[Progress])

  override def receive: Receive = {
    case Optimize(forge, simulation, experiment, temperature, strainRate) =>
      val function = FitnessFunction(forge, simulation, temperature, context.system, experiment, KGF, progress)

      val bounds = Seq(
        StaticInterval(1200, 1600), //a1
        StaticInterval(-0.001, -0.003), //m1
        StaticInterval(-0.1, -0.25), //m2
        StaticInterval(0.12, 0.16), //m3
        StaticInterval(-0.04, -0.065), //m4
        StaticInterval(0.0), //m5
        StaticInterval(0.0), //m6
        StaticInterval(0.0), //m7
        StaticInterval(0.0), //m8
        StaticInterval(0.0), //m9
        StaticInterval(0.0) //epSS
      )

      def fit(sx: Seq[Double]): Double = {
        function.fitness(sx)
      }

      val optimizer = GreyWolfOptimizer(fit, bounds)
      val wolfs = 10
      val iterations = 10

      //create new progress bar for minimize
      Util.time {
        progress ! ui.controls.ProgressProtocol.SetStart(System.nanoTime(), wolfs * iterations)
        val min = optimizer.min(wolfs, iterations)
        progress ! ui.controls.ProgressProtocol.SetEnd(temperature, strainRate, HSArgs(min))
      }
  }
}
