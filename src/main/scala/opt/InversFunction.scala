package opt

import java.nio.file.Path

import akka.actor.{ Actor, ActorLogging }
import akka.pattern.ask
import akka.util.Timeout
import data.{ Samples, DataContainer, Force, ResultContainer }
import io.forge.Protocol.{ Job, Parameters }
import opt.InversFunction.SetData
import reo.HSArgs
import util.XORShiftRandom

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class InversFunction extends Actor with ActorLogging {
  implicit val timeout = Timeout(20 minutes)

  val supervisor = context.actorSelection("akka://sinvers/user/supervisor")

  val random = new XORShiftRandom()

  override def receive: Receive = toSet

  def toSet: Receive = {
    case SetData(forge, sample, temperature, data, conversion) =>
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
        val fitness = result.score(interpolator, interval)

        fitness
      }
      sender() ! FunctionContainer(fitness)
  }
}

object InversFunction {
  case class SetData(forge: Path,
    sample: Samples,
    temperature: Double,
    data: DataContainer,
    conversion: Force)
  case object GetFunction
}