package ui.controls

import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.scene.layout.BorderPane
import scalafx.stage.Stage

object GUI {
  val pane = new BorderPane {

  }

  val stage = new Stage {
    title = "sinvers"
    minWidth = 1024
    scene = new Scene {
      root = pane
    }
    onCloseRequest = handle {
      System.exit(0)
    }
  }

  def printEvent(eventStr: String)() {
    println(eventStr)
  }

  stage.show()
}
