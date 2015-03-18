package ui.controls

import akka.actor.{ Actor, ActorLogging }

import scala.language.postfixOps
import scalafx.geometry.Insets
import scalafx.scene.control.ProgressBar
import scalafx.scene.layout.HBox
import scalafx.scene.text.Text

class Progress extends Actor with ActorLogging {

  import ui.controls.ProgressBarProtocol._

  val progressBar = new ProgressBar() {
    minWidth = 750
    progress = 0
    stylesheets add "css/progress-bar.css"
  }

  val eta = new Text {
    wrappingWidth = 750
  }

  val progress = new HBox {
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
        val performance = iterations * 1e9 / (stamp - start)
        val duration = (max - iterations) * performance
        s"ETA: ${formatter(duration)} s performance: ${formatter(performance)} it/s"
      }
    case Reset =>
      progressBar.setProgress(0)
      context become toSet
  }

  def toSet: Receive = {
    case Set(start, max) => context become set(start, max)
  }

  def formatter(d: Double): String = new java.text.DecimalFormat("0.###").format(d)

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    GUI.pane.setBottom(progress)
  }
}

object ProgressBarProtocol {

  case class Increment(stamp: Long)

  case class Set(start: Long, max: Double)

  case object Reset

}
