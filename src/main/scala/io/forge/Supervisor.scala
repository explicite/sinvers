package io.forge

import akka.actor.SupervisorStrategy.Stop
import akka.actor._
import akka.routing.{ ActorRefRoutee, RoundRobinRoutingLogic, Router }
import io.forge.Protocol._

import scala.concurrent.duration._

class Supervisor
    extends Actor
    with ActorLogging {
  context.setReceiveTimeout(30 seconds)

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 0, withinTimeRange = 500 millisecond) {
    case _ => Stop
  }

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
      router.route(job, sender())
    case Terminated(a) =>
      log.debug(s"terminated:$a")
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[Worker])
      context watch r
      router = router.addRoutee(r)
  }
}
