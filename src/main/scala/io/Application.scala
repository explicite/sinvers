package io

import akka.actor.{ ActorSystem, Props }
import data.DataFile
import opt.InversFunction
import ui.DONConfigurator
import ui.controls.ProgressBar

trait Application {
  val system = ActorSystem("sinvers")
  val DONConfigurator = system.actorOf(Props[DONConfigurator].withDispatcher("scalafx-dispatcher"), "don-configurator")
  val progressBar = system.actorOf(Props[ProgressBar].withDispatcher("scalafx-dispatcher"), "progress-bar")
  //val fitnessChart = system.actorOf(Props[FitnessChart].withDispatcher("scalafx-dispatcher"), "fitness-chart")

  val fx2Dir = "C:\\Users\\Jan\\Desktop\\Forge2-V3.0"
  val workingDirectory = "C:\\Users\\Jan\\Desktop\\sym"
  val process = Forge(fx2Dir)

  val experimentDirectory = "C:\\Users\\Jan\\Desktop\\computed.txt"
  //val experimentDirectory = "C:\\Users\\Jan\\Desktop\\mgr\\HA000490.D01"
  val don = DON(new java.io.File(s"$workingDirectory\\newSym.don"))
  val experimentData = DataFile(new java.io.File(experimentDirectory))
  val function = InversFunction(process, don, experimentData, system)
}
