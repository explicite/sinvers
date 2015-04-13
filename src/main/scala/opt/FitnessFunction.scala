package opt

import java.nio.file.Path

import akka.actor.{ ActorRef, ActorSystem }
import akka.pattern.ask
import akka.util.Timeout
import data.{ Samples, DataContainer, Force, ResultContainer }
import io.forge.Protocol.{ Job, Parameters }
import reo.HSArgs
import ui.Protocol.Iteration
import util.XORShiftRandom

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

case class FitnessFunction(forge: Path,
    sample: Samples,
    temperature: Double,
    system: ActorSystem,
    data: DataContainer,
    conversion: Force,
    progress: ActorRef) {
  implicit val timeout = Timeout(20 minutes)

  val supervisor = system.actorSelection("akka://sinvers/user/supervisor")

  val random = new XORShiftRandom()

  val interval = {
    val max = data.jaw.max + sample.max
    val min = data.jaw.min + sample.max
    StaticInterval(min, max)
  }

  val interpolatorInterval = {
    StaticInterval(data.jaw.min - 0.01, data.jaw.max + 0.01)
  }

  val preparedData = data.slice(interpolatorInterval)
  val interpolator = preparedData.interpolator(conversion, sample.max)
  val steering = preparedData.steering(sample.max)

  //return fitness for current context
  def fitness(args: Seq[Double]): Double = {
    val parameters = Parameters(sample, steering, interval, temperature, HSArgs(args))
    val request = (supervisor ? Job(forge, parameters)).mapTo[ResultContainer]

    val result = Await.result(request, timeout.duration)
    val fitness = result.slice(interval).fit(interpolator)

    progress ! Iteration(fitness, System.nanoTime())
    fitness
  }

}
