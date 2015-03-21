package io.forge

import akka.actor.{ Actor, ActorLogging }
import data.Data
import io.forge.Protocol.Job

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }
import scala.util.{ Failure, Success, Try }

class Worker
    extends Actor
    with Environment
    with ActorLogging {

  def receive = {
    case Job(forge, parameters) =>
      environment = environment(forge, parameters)
      log.debug(s"$uuid| start job for:$environment)")
      val builder = processBuilder(forge, environment)
      log.debug(s"$uuid| prepared job for forge:$forge")
      val io = processIO
      process = builder.run(io)
      log.debug(s"$uuid| forge process created:$process")
      val feature = Future(body = process.exitValue() match {
        case 0 =>
          log.debug(s"$uuid| end of computation")
          clean(environment)
          Data(time.toList, load.toList, height.toList, velocity.toList)
      })

      val result = Try(Await.result(feature, 30 seconds)) match {
        case Success(data) => data
        case Failure(err) =>
          clean(environment)
          Data.empty
      }

      sender() ! result
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    log.debug(s"$environment| destroy after stop and cleanup")
    if (process != null) {
      process.destroy()
    }
    super.postStop()
  }
}

