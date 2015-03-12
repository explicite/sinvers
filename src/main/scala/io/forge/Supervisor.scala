package io.forge

import akka.actor.{ Actor, ActorLogging, Props, Terminated }
import akka.routing.{ ActorRefRoutee, RoundRobinRoutingLogic, Router }
import io.forge.Protocol.Job

import scala.concurrent.duration._

class Supervisor
    extends Actor
    with ActorLogging {
  context.setReceiveTimeout(30 seconds)

  var router = {
    val routees = Vector.fill(4) {
      val r = context.actorOf(Props[Worker])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case job: Job =>
      log.info(s"route job to worker")
      router.route(job, sender())
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[Worker])
      context watch r
      router = router.addRoutee(r)
  }
}
