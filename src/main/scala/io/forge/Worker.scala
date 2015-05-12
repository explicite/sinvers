package io.forge

import akka.actor.{ Actor, ActorLogging }
import data.ResultContainer
import io.forge.Protocol.Job

import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }
import scala.language.postfixOps
import scala.sys.process.{ Process, ProcessIO }
import scala.util.{ Success, Try }

class Worker
    extends Actor
    with Environment
    with ActorLogging {

  implicit val executionContext = context.system.dispatchers.lookup("worker-dispatcher")

  def receive = {
    case Job(forge, parameters) =>
      time.clear()
      load.clear()
      height.clear()
      velocity.clear()
      val environment = createEnvironment(forge, parameters)
      val builder = processBuilder(forge, environment)

      var process: Process = null
      val processIO = new ProcessIO(
        in => (),
        data => try {
          scala.io.Source.fromInputStream(data).getLines().foreach(processDataLine)
        } finally {
          data.close()
        },
        error => ()
      )
      process = builder.run(processIO)

      val future = Future(body = process.exitValue() match {
        case 0 => ResultContainer(time, load, height, velocity)
      })

      val result = Try(Await.result(future, 15 seconds)) match {
        case Success(data) => data
        case _             => ResultContainer.empty
      }
      process.destroy()
      clean(environment)
      sender() ! result
  }

  private def processDataLine(line: String): Unit = {
    IncrementTimeRegex findFirstIn line match {
      case Some(IncrementTimeRegex(_, mantissa, exponent)) => time += formatDouble(mantissa, exponent)
      case None => Unit
    }
    VirtualLoadRegex findFirstIn line match {
      case Some(VirtualLoadRegex(_, mantissa, exponent)) => load += formatDouble(mantissa, exponent)
      case None => Unit
    }
    HeightRegex findFirstIn line match {
      case Some(HeightRegex(_, mantissa, exponent)) => height += formatDouble(mantissa, exponent)
      case None                                     => Unit
    }
    VelocityRegex findFirstIn line match {
      case Some(VelocityRegex(_, mantissa, exponent)) => velocity += formatDouble(mantissa, exponent)
      case None                                       => Unit
    }
  }

}

