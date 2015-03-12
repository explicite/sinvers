package opt

import java.io.File
import java.nio.file.Paths

import akka.actor.{ ActorSystem, Props }
import akka.pattern.ask
import akka.util.Timeout
import data.{ Data, DataFile }
import io.forge.Protocol.Job
import io.forge.{ Protocol, Supervisor }
import io.{ DON, Forge }
import math._
import reo.HSArgs
import util.{ Persist, XORShiftRandom }

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{ Failure, Success, Try }

case class InversFunction(forge: Forge, originalDon: DON, data: DataFile) {
  val system = ActorSystem("forge")
  val supervisor = system.actorOf(Props[Supervisor], "supervisor")

  val random = new XORShiftRandom()
  val current = data.current

  val steering: Seq[(Double, Double)] = {
    val velocityStep = current.velocity.scan(0d)(_ + _).tail
    val jaw = current.jaw
    velocityStep.zip(jaw)
  }

  val pilotage = {
    val file = new File(s"${originalDon.workingDirectory}//pilotage.dat")
    Persist.zipped(steering, file)
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
    implicit val timeout = Timeout(20 seconds)
    val feature = supervisor ? Job(Paths.get(forge.xf2Dir), Paths.get(originalDon.workingDirectory), HSArgs(args))
    val computed = Try(Await.result(feature, timeout.duration)) match {
      case Success(result) => result.asInstanceOf[Data]
      case Failure(ex) => Data.empty
    }

    val fit = computed.fit(interpolator, interval)
    println(s"fitness:$fit")
    fit
  }

}
