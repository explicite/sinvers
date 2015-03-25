package opt

import java.nio.file.Path

import akka.actor.{ ActorSystem, Props }
import akka.pattern.ask
import akka.util.Timeout
import data.{ DataContainer, DataFile }
import io.forge.Protocol.{ Job, Parameters }
import io.forge.Supervisor
import math._
import reo.HSArgs
import util.XORShiftRandom

import scala.concurrent.Await
import scala.concurrent.duration._

case class FitnessFunction(forge: Path,
    mesh: Path,
    out: Path,
    steering: Path,
    temperature: Double,
    system: ActorSystem,
    data: DataFile) {
  implicit val timeout = Timeout(20 minutes)

  val supervisor = system.actorOf(Props[Supervisor], "supervisor")
  val progressBar = system.actorSelection("/user/progress-bar")
  val fitnessChart = system.actorSelection("/user/fitness-chart")
  progressBar ! ui.controls.ProgressBarProtocol.Reset
  fitnessChart ! ui.controls.FitnessChartProtocol.Reset

  val random = new XORShiftRandom()

  val steeringDef: Seq[(Double, Double)] = {
    val velocityStep = data.current.velocity.scan(0d)(_ + _).tail
    val jaw = data.current.jaw
    velocityStep.zip(jaw)
  }

  val interval = {
    val max = 12 + 0.1
    val min = 7.522 - 0.1
    StaticInterval(min, max)
  }

  val interpolator = {
    val force = data.current.force
    val jaw = data.current.jaw //.map(12d + _)

    val (filteredForce, filteredJaw) = force.zip(jaw).filter { case (f, j) => j >= interval.min && j <= interval.max }.groupBy(_._2).map(_._2.head).toSeq.sortBy(_._2).unzip
    //val splineInterpolator = Interpolator.splineInterpolate(filteredJaw.toArray, KZ(filteredForce.toArray, 100, 2).toArray)
    val splineInterpolator = Interpolator.splineInterpolate(filteredJaw.toArray, filteredForce.toArray)

    splineInterpolator
  }

  //return fitness for current context
  def fitness(args: Seq[Double]): Double = {
    val parameters = Parameters(mesh, out, steering, temperature, HSArgs(args))
    val request = (supervisor ? Job(forge, parameters)).mapTo[DataContainer]

    val result = Await.result(request, timeout.duration)
    val fitness = result.fit(interpolator, interval)

    progressBar ! ui.controls.ProgressBarProtocol.Increment(System.nanoTime())
    fitnessChart ! ui.controls.FitnessChartProtocol.Iteration(fitness)
    fitness
  }

}
