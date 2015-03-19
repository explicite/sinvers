package io

import akka.actor.{ ActorSystem, Props }
import data.DataFile
import opt.FitnessFunction
import ui.controls.{ FitnessChart, Progress }

import java.nio.file.Paths

trait Application {
  val system = ActorSystem("sinvers")
  val progress = system.actorOf(Props[Progress].withDispatcher("scalafx-dispatcher"), "progress-bar")
  val chart = system.actorOf(Props[FitnessChart].withDispatcher("scalafx-dispatcher"), "fitness-chart")

  val fx2Dir = Paths.get("C:\\Users\\Jan\\Desktop\\Forge2-V3.0")
  val source = Paths.get("C:\\Users\\Jan\\Desktop\\sym")

  val experimentDirectory = "C:\\Users\\Jan\\Desktop\\computed.txt"
  //val experimentDirectory = "C:\\Users\\Jan\\Desktop\\mgr\\HA000490.D01"
  val experiment = DataFile(new java.io.File(experimentDirectory), temperature = 1000, steering = "pilotage.dat")
  val function = FitnessFunction(fx2Dir, source, system, experiment, experiment)
}
