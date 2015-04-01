package ui.controls

import java.time.Duration

import akka.actor.{ Actor, ActorLogging }
import ui.Protocol._

import scala.language.postfixOps
import scala.math.abs
import scalafx.geometry.Insets
import scalafx.scene.control.ProgressBar
import scalafx.scene.layout.VBox
import scalafx.scene.text.Text

class Progress extends Actor with ActorLogging {

  import ui.controls.ProgressBarProtocol._

  val gui = context.actorSelection("akka://sinvers/user/gui")

  val progressBar = new ProgressBar() {
    prefWidth = 200
    minWidth = 200
    maxWidth = 200
    progress = 0
    stylesheets add "css/progress-bar.css"
  }

  val eta = new Text {}

  val progress = new VBox {
    prefWidth = 240
    minWidth = 240
    maxWidth = 240
    padding = Insets(20)
    spacing = 20
    children = Seq(progressBar, eta)
  }

  def receive = toSet

  def set(start: Long, max: Double): Receive = {
    case Iteration(_, stamp) =>
      progressBar.setProgress(progressBar.progress.value + 1 / max)
      eta.text = {
        val iterations = max * progressBar.progress.value
        val performance = ((stamp - start) / 1e9) / iterations
        val duration = Duration.ofSeconds(((max - iterations) * performance).toLong)
        s"ETA: $duration s performance: ${formatter(1 / performance)} it/s"
      }
    case Reset =>
      gui ! RemoveProgress(progress)
      progressBar.setProgress(0)
      context become toSet
  }

  def toSet: Receive = {
    case Set(start, max) =>
      gui ! AddProgress(progress)
      context become set(start, max)
  }

  def formatter(d: Double): String = new java.text.DecimalFormat("0.###").format(abs(d))

}

object ProgressBarProtocol {

  case class Set(start: Long, max: Double)

}
