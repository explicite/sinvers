package ui.controls

import java.time.Duration

import akka.actor.{ Actor, ActorLogging }
import ui.Protocol._

import scala.language.postfixOps
import scala.math.abs
import scalafx.geometry.Insets
import scalafx.scene.control.{ Button, ProgressBar }
import scalafx.scene.image.{ Image, ImageView }
import scalafx.scene.layout.{ HBox, VBox }
import scalafx.scene.paint.Color
import scalafx.scene.text.Text

class Progress extends Actor with ActorLogging {

  import ui.controls.ProgressProtocol._

  val gui = context.actorSelection("akka://sinvers/user/gui")

  val progressBar = new ProgressBar() {
    prefWidth = 200
    minWidth = 200
    maxWidth = 200
    progress = 0
    stylesheets add "css/progress-bar.css"
  }

  val eta = new Text {}

  val saveButton = new Button {
    graphic = new ImageView {
      image = new Image(getClass.getResourceAsStream("/css/icon_check_alt.png"))
    }
  }

  val removeButton = new Button {
    graphic = new ImageView {
      image = new Image(getClass.getResourceAsStream("/css/icon_close_alt.png"))
    }
  }

  val box = new HBox {
    children = List(eta, saveButton, removeButton)
  }

  val progress = new VBox {
    prefWidth = 240
    minWidth = 240
    maxWidth = 240
    padding = Insets(20)
    spacing = 20
    children = Seq(progressBar, box)
    style = "" +
      "-fx-background-color: white;" +
      "-fx-border-color: black;" +
      "-fx-border-width: 1;" +
      "-fx-border-radius: 6;" +
      "-fx-padding: 6;"
  }

  def receive = toSet

  def set(start: Long, max: Double): Receive = {
    case Iteration(_, stamp) =>
      progressBar.setProgress(progressBar.progress.value + 1 / max)
      eta.text = {
        val iterations = max * progressBar.progress.value
        val performance = ((stamp - start) / 1e9) / iterations
        val duration = Duration.ofSeconds(((max - iterations) * performance).toLong)
        s"ETA: $duration s perf: ${formatter(1 / performance)} it/s"
      }
    case Reset =>
      gui ! Remove(progress)
      progressBar.setProgress(0)
      context become toSet
  }

  def toSet: Receive = {
    case SetStart(start, max) =>
      gui ! Add(progress)
      context become set(start, max)
  }

  def formatter(d: Double): String = new java.text.DecimalFormat("0.###").format(abs(d))

}

object ProgressProtocol {

  case class SetStart(start: Long, max: Double)

}
