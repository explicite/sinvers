package io

import akka.actor.{ ActorSystem, Props }
import data.{ KGF, DataContainer }
import opt.FitnessFunction
import ui.controls.{ FitnessChart, Progress }

import java.nio.file.Paths

trait Application {
  val system = ActorSystem("sinvers")
  val progress = system.actorOf(Props[Progress].withDispatcher("scalafx-dispatcher"), "progress-bar")
  val chart = system.actorOf(Props[FitnessChart].withDispatcher("scalafx-dispatcher"), "fitness-chart")

  val fx2Dir = Paths.get("C:\\Users\\Jan\\Desktop\\Forge2-V3.0")
  val source = Paths.get("C:\\Users\\Jan\\Desktop\\sym")

  val steering = source.resolve("pilotage.dat")
  val mesh = source.resolve("work.may")
  val out = source.resolve("file.out")
  val experimentDirectory = "C:\\Users\\Jan\\Desktop\\mgr\\HA000490.D01"
  val experiment = DataContainer(new java.io.File(experimentDirectory))
  val function = FitnessFunction(fx2Dir, mesh, out, 1000, system, experiment, KGF)
}
