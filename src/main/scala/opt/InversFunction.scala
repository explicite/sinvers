package opt

import java.nio.file.Paths

import akka.actor.{ ActorSystem, Props }
import akka.pattern.ask
import akka.util.Timeout
import data.{ Data, DataFile }
import io.forge.Protocol.Job
import io.forge.Supervisor
import io.{ DON, Forge }
import math._
import reo.HSArgs
import util.XORShiftRandom
import akka.actor.PoisonPill
import akka.routing.Broadcast

import scala.concurrent.Await
import scala.concurrent.duration._

case class InversFunction(forge: Forge, originalDon: DON, data: DataFile, system: ActorSystem) {
  implicit val timeout = Timeout(20 minutes)

  val supervisor = system.actorOf(Props[Supervisor], "supervisor")
  val progressBar = system.actorSelection("/user/progress-bar")

  val random = new XORShiftRandom()
  val current = data.current

  val steering: Seq[(Double, Double)] = {
    val velocityStep = current.velocity.scan(0d)(_ + _).tail
    val jaw = current.jaw
    velocityStep.zip(jaw)
  }

  val interval = {
    val max = 12 + 0.1
    val min = 7.522 - 0.1
    StaticInterval(min, max)
  }

  val interpolator: PolynomialSplineFunction = {
    val force = current.force
    val jaw = current.jaw //.map(12d + _)

    val (filteredForce, filteredJaw) = force.zip(jaw).filter { case (f, j) => j >= interval.min && j <= interval.max }.groupBy(_._2).map(_._2.head).toSeq.sortBy(_._2).unzip
    //val splineInterpolator = Interpolator.splineInterpolate(filteredJaw.toArray, KZ(filteredForce.toArray, 100, 2).toArray)
    val splineInterpolator = Interpolator.splineInterpolate(filteredJaw.toArray, filteredForce.toArray)

    splineInterpolator
  }

  //return fitness for current context
  def fitness(args: Seq[Double]): Double = {
    val request = (supervisor ? Job(Paths.get(forge.xf2Dir), Paths.get(originalDon.workingDirectory), HSArgs(args))).mapTo[Data]
    val result = Await.result(request, timeout.duration)
    progressBar ! ui.controls.ProgressBarProtocol.Increment
    result.fit(interpolator, interval)
  }

  def stop = ???

  def start = ???

}
