package opt

import java.nio.file.Path

import akka.actor.{ ActorRef, ActorSystem }
import akka.pattern.ask
import akka.util.Timeout
import data.{ DataContainer, Force, ResultContainer }
import io.forge.Protocol.{ Job, Parameters }
import reo.HSArgs
import ui.Protocol.Iteration
import util.XORShiftRandom

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

case class FitnessFunction(forge: Path,
    mesh: Path,
    out: Path,
    temperature: Double,
    system: ActorSystem,
    data: DataContainer,
    conversion: Force,
    progress: ActorRef) {
  implicit val timeout = Timeout(20 minutes)

  val supervisor = system.actorSelection("akka://sinvers/user/supervisor")

  val random = new XORShiftRandom()

  val maxJaw = 12
  val margin = 0.1
  val interval = {
    //val max = 12 + 0.1
    val max = 12 - maxJaw + 0.01
    //val min = 7.522 - 0.1
    val min = 7.522 - maxJaw - margin
    StaticInterval(min, max)
  }

  val preparedData = data.slice(interval)
  val interpolator = preparedData.interpolator(conversion, maxJaw)
  val steering = preparedData.steering(maxJaw)

  //return fitness for current context
  def fitness(args: Seq[Double]): Double = {
    val parameters = Parameters(mesh, out, steering, temperature, HSArgs(args))
    val request = (supervisor ? Job(forge, parameters)).mapTo[ResultContainer]

    val result = Await.result(request, timeout.duration)
    val fitness = result.slice(StaticInterval(7.522, 12)).fit(interpolator)

    progress ! Iteration(fitness, System.nanoTime())
    fitness
  }

}
