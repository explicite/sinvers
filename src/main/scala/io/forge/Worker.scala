package io.forge

import akka.actor.{ Actor, ActorLogging }
import data.DataContainer
import io.forge.Protocol.Job

import scala.util.{ Failure, Success, Try }

class Worker
    extends Actor
    with Environment
    with ActorLogging {

  def receive = {
    case Job(forge, parameters) =>
      val environment = createEnvironment(forge, parameters)
      val builder = processBuilder(forge, environment)
      val result = Try(process(builder.lineStream_!(silence))) match {
        case Success((time, load, height, velocity)) => DataContainer(time, load, height, velocity)
        case Failure(err)                            => DataContainer.empty
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

