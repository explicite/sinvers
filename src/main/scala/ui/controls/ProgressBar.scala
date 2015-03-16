package ui.controls

import akka.actor.{ Actor, ActorLogging }

import scala.language.postfixOps
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{ ProgressBar => FXProgressBar }
import scalafx.scene.layout.BorderPane
import scalafx.scene.text.Text
import scalafx.stage.Stage

class ProgressBar extends Actor with ActorLogging {

  import ui.controls.ProgressBarProtocol._

  var max: Double = 0
  var start = System.nanoTime()

  def step() = 1 / max

  val progressBar = new FXProgressBar() {
    minWidth = 750
    progress = 0
    stylesheets add "css/progress-bar.css"
  }

  var eta = new Text {
    wrappingWidth = 750
    text = ""
  }
  val stage = new Stage {
    outer =>
    title = "Progress"
    scene = new Scene {
      root = new BorderPane {
        padding = Insets(25)
        center = progressBar
        bottom = eta
      }
    }
  }

  def receive = {
    case Increment =>
      progressBar.setProgress(progressBar.progress.value + step())
      eta.text = {
        val stop = System.nanoTime()
        val iterations = max * progressBar.progress.value
        val performance = iterations / ((stop - start) / 1e10)
        val duration = (max - iterations) * performance
        s"ETA: $duration s, performance: $performance it/s"
      }

    case Set(newMax) =>
      stage.close()
      max = newMax
      start = System.nanoTime()
      progressBar.setProgress(0)
      stage.show()
  }

}

object ProgressBarProtocol {

  case object Increment

  case class Set(max: Double)

}
