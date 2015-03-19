package opt

import akka.actor.{ ActorSystem, Props }
import akka.pattern.ask
import akka.util.Timeout
import data.{ Data, DataFile }
import io.DONArgs
import io.forge.Protocol.Job
import io.forge.Supervisor
import math._
import reo.HSArgs
import util.XORShiftRandom

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Future, Await }
import scala.concurrent.duration._
import java.nio.file.Path

case class FitnessFunction(forge: Path, source: Path, system: ActorSystem, dataList: DataFile*) {
  implicit val timeout = Timeout(20 minutes)

  val supervisor = system.actorOf(Props[Supervisor], "supervisor")
  val progressBar = system.actorSelection("/user/progress-bar")
  val fitnessChart = system.actorSelection("/user/fitness-chart")
  progressBar ! ui.controls.ProgressBarProtocol.Reset
  fitnessChart ! ui.controls.FitnessChartProtocol.Reset

  val random = new XORShiftRandom()

  val steering: Seq[(Double, Double)] = {
    val velocityStep = dataList.head.current.velocity.scan(0d)(_ + _).tail
    val jaw = dataList.head.current.jaw
    velocityStep.zip(jaw)
  }

  val interval = {
    val max = 12 + 0.1
    val min = 7.522 - 0.1
    StaticInterval(min, max)
  }

  def interpolator(data: DataFile) = {
    val force = data.current.force
    val jaw = data.current.jaw //.map(12d + _)

    val (filteredForce, filteredJaw) = force.zip(jaw).filter { case (f, j) => j >= interval.min && j <= interval.max }.groupBy(_._2).map(_._2.head).toSeq.sortBy(_._2).unzip
    //val splineInterpolator = Interpolator.splineInterpolate(filteredJaw.toArray, KZ(filteredForce.toArray, 100, 2).toArray)
    val splineInterpolator = Interpolator.splineInterpolate(filteredJaw.toArray, filteredForce.toArray)

    splineInterpolator
  }

  val interpolators = dataList.map(interpolator)

  //return fitness for current context
  def fitness(args: Seq[Double]): Double = {
    val requests = Future.sequence(dataList.zip(interpolators).map {
      case (dataFile, interpolator) =>
        val donArgs = DONArgs(HSArgs(args), dataFile.temperature, dataFile.steering)
        (supervisor ? Job(forge, source, donArgs)).mapTo[Data].map {
          result =>
            result.fit(interpolator, interval)
        }
    })

    val results = Await.result(requests, timeout.duration)
    val fitness = results.sum

    progressBar ! ui.controls.ProgressBarProtocol.Increment(System.nanoTime())
    fitnessChart ! ui.controls.FitnessChartProtocol.Iteration(fitness)
    fitness
  }

}
