package opt

import java.nio.file.Path

import akka.actor.{ ActorSystem, Props }
import akka.pattern.ask
import akka.util.Timeout
import data.{ ResultContainer, DataContainer }
import io.forge.Protocol.{ Job, Parameters }
import io.forge.Supervisor
import reo.HSArgs
import util.XORShiftRandom

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

case class FitnessFunction(forge: Path,
    mesh: Path,
    out: Path,
    steering: Path,
    temperature: Double,
    system: ActorSystem,
    data: DataContainer) {
  implicit val timeout = Timeout(20 minutes)

  val supervisor = system.actorOf(Props[Supervisor], "supervisor")
  val progressBar = system.actorSelection("/user/progress-bar")
  val fitnessChart = system.actorSelection("/user/fitness-chart")
  progressBar ! ui.controls.ProgressBarProtocol.Reset
  fitnessChart ! ui.controls.FitnessChartProtocol.Reset

  val random = new XORShiftRandom()

  val interval = {
    val max = 12 + 0.1
    val min = 7.522 - 0.1
    StaticInterval(min, max)
  }

  val interpolator = data.slice(interval).interpolator

  //return fitness for current context
  def fitness(args: Seq[Double]): Double = {
    val parameters = Parameters(mesh, out, steering, temperature, HSArgs(args))
    val request = (supervisor ? Job(forge, parameters)).mapTo[ResultContainer]

    val result = Await.result(request, timeout.duration)
    val fitness = result.fit(interpolator, interval)

    progressBar ! ui.controls.ProgressBarProtocol.Increment(System.nanoTime())
    fitnessChart ! ui.controls.FitnessChartProtocol.Iteration(fitness)
    fitness
  }

}
