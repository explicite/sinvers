package io.forge

import akka.actor.{ Actor, ActorLogging }
import data.ResultContainer
import io.forge.Protocol.Job

import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }
import scala.language.postfixOps
import scala.util.{ Failure, Success, Try }

class Worker
    extends Actor
    with Environment
    with ActorLogging {

  implicit val executionContext = context.system.dispatchers.lookup("worker-dispatcher")

  def receive = {
    case Job(forge, parameters) =>
      val environment = createEnvironment(forge, parameters)
      val builder = processBuilder(forge, environment)
      val result = Try(Await.result(Future { process(builder.lineStream_!(silence)) }, 30 seconds)) match {
        case Success((time, load, height, velocity)) => ResultContainer(time, load, height, velocity)
        case Failure(err)                            => ResultContainer.empty
      }
      clean(environment)
      sender() ! result
  }

  def process(stream: Stream[String]): (List[Double], List[Double], List[Double], List[Double]) = {
    stream.foldLeft((List.empty[Double], List.empty[Double], List.empty[Double], List.empty[Double])) {
      case ((time, load, height, velocity), line) =>
        val accTime = IncrementTimeRegex findFirstIn line match {
          case Some(IncrementTimeRegex(_, mantissa, exponent)) => time :+ formatDouble(mantissa, exponent)
          case None => time
        }
        val accLoad = VirtualLoadRegex findFirstIn line match {
          case Some(VirtualLoadRegex(_, mantissa, exponent)) => load :+ formatDouble(mantissa, exponent)
          case None => load
        }
        val accHeight = HeightRegex findFirstIn line match {
          case Some(HeightRegex(_, mantissa, exponent)) => height :+ formatDouble(mantissa, exponent)
          case None                                     => height
        }
        val accVelocity = VelocityRegex findFirstIn line match {
          case Some(VelocityRegex(_, mantissa, exponent)) => velocity :+ formatDouble(mantissa, exponent)
          case None                                       => velocity
        }
        (accTime, accLoad, accHeight, accVelocity)
    }
  }

}

