package ui.controls

import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.layout.GridPane
import scalafx.stage.Stage

object GUI {
  val pane = new GridPane {
    prefWidth = 1024
    prefHeight = 640
    padding = Insets(18)
  }

  val stage = new Stage {
    title = "sinvers"
    width = 864
    height = 550
    scene = new Scene {
      root = pane
    }
    onCloseRequest = handle {
      System.exit(0)
    }
  }

  stage.show()
}
