package ui.controls

import java.time.Duration

import akka.actor.{ Actor, ActorLogging }
import ui.Protocol.{ Iteration, Reset, Unregister, Register }

import scala.language.postfixOps
import scala.math.abs
import scalafx.geometry.Insets
import scalafx.scene.control.ProgressBar
import scalafx.scene.layout.HBox
import scalafx.scene.text.Text

class Progress extends Actor with ActorLogging {

  import ui.controls.ProgressBarProtocol._

  val progressBar = new ProgressBar() {
    minWidth = 500
    progress = 0
    stylesheets add "css/progress-bar.css"
  }

  val eta = new Text {}

  val progress = new HBox {
    minWidth = 750
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
      context.parent ! Unregister(progress)
      progressBar.setProgress(0)
      context become toSet
  }

  def toSet: Receive = {
    case Set(start, max) =>
      context.parent ! Register(progress, 1, 2)
      context become set(start, max)
  }

  def formatter(d: Double): String = new java.text.DecimalFormat("0.###").format(abs(d))

}

object ProgressBarProtocol {

  case class Set(start: Long, max: Double)

}
