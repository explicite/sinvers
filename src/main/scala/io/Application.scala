package io

import akka.actor.{ ActorSystem, Props }
import io.forge.Supervisor
import ui.controls.GUI

trait Application {
  val system = ActorSystem("sinvers")
  val supervisor = system.actorOf(Props[Supervisor], "supervisor")
  val gui = system.actorOf(Props[GUI], "gui")
}