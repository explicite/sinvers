package ui.controls

import java.time.Duration

import akka.actor.{ Actor, ActorLogging }

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
    case Increment(stamp) =>
      progressBar.setProgress(progressBar.progress.value + 1 / max)
      eta.text = {
        val iterations = max * progressBar.progress.value
        val performance = ((stamp - start) / 1e9) / iterations
        val duration = Duration.ofSeconds(((max - iterations) * performance).toLong)
        s"ETA: $duration s performance: ${formatter(1 / performance)} it/s"
      }
    case Reset =>
      progressBar.setProgress(0)
      context become toSet
  }

  def toSet: Receive = {
    case Set(start, max) => context become set(start, max)
  }

  def formatter(d: Double): String = new java.text.DecimalFormat("0.###").format(abs(d))

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    GUI.pane.add(progress, 1, 2)
  }
}

object ProgressBarProtocol {

  case class Increment(stamp: Long)

  case class Set(start: Long, max: Double)

  case object Reset

}
