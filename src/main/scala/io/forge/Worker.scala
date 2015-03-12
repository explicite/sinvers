package io.forge

import akka.actor.{ Actor, ActorLogging }
import data.Data
import io.forge.Protocol.{Result, Job}

class Worker
    extends Actor
    with Environment
    with ActorLogging {

  def receive = {
    case Job(forge, source, args) =>
      log.info(s"$environment | start job")
      environment = environment(forge, source)
      val builder = processBuilder(forge, environment, args)
      log.info(s"$environment | prepared job for")
      val io = processIO
      process = builder.run(io)
      log.info(s"$environment | forge process created")
      process.exitValue()
      log.info(s"$environment | end of computation")
      sender() ! Result(Data(time, load, height, velocity))
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    if (process != null)
      process.destroy()
    super.postStop()
  }
}

