package ui.controls

import akka.actor.{Actor, ActorLogging}

import scala.language.postfixOps
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{ProgressBar => FXProgressBar}
import scalafx.scene.layout.BorderPane
import scalafx.stage.Stage

class ProgressBar extends Actor with ActorLogging {

  import ui.controls.ProgressBarProtocol._

  var max: Double = 0

  def step() = 1 / max

  val progressBar = new FXProgressBar() {
    minWidth = 750
    progress = 0
    stylesheets add "css/progress-bar.css"
  }

  val stage = new Stage {
    outer =>
    title = "Progress"
    scene = new Scene {
      root = new BorderPane {
        padding = Insets(25)
        bottom = progressBar
      }
    }
  }

  def receive = {
    case Increment =>
      progressBar.setProgress(progressBar.progress.value + step())
    case Set(newMax) =>
      stage.close()
      max = newMax
      progressBar.setProgress(0)
      stage.show()
  }

}

object ProgressBarProtocol {

  case object Increment

  case class Set(max: Double)

}
