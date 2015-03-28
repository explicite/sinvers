package ui.controls

import java.io.File

import akka.actor.{ Actor, ActorLogging }

import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox
import scalafx.stage.FileChooser

class SimulationConfigurator extends Actor with ActorLogging {
  val pane = new VBox() {
    padding = Insets(20)
    spacing = 10
  }

  var forge: File = null
  val forgeDirButton = new Button {
    id = "forgeDirButton"
    onAction = (ae: ActionEvent) => {
      val fileChooser = new FileChooser() {
        title = "Pick a forge dir"
      }
      forge = fileChooser.showOpenDialog(pane.getParent.getScene.window())
    }
  }

  var mesh: File = null
  val meshDirButton = new Button {
    id = "meshDirButton"
    onAction = (ae: ActionEvent) => {
      val fileChooser = new FileChooser() {
        title = "Pick a mesh dir"
      }
      mesh = fileChooser.showOpenDialog(pane.getParent.getScene.window())
    }
  }

  var out: File = null
  val outDirButton = new Button {
    id = "outDirButton"
    onAction = (ae: ActionEvent) => {
      val fileChooser = new FileChooser() {
        title = "Pick a out dir"
      }
      out = fileChooser.showOpenDialog(pane.getParent.getScene.window())
    }
  }

  var experiment: File = null
  val experimentDirButton = new Button {
    id = "experimentDirButton"
    onAction = (ae: ActionEvent) => {
      val fileChooser = new FileChooser() {
        title = "Pick a experiment dir"
      }
      experiment = fileChooser.showOpenDialog(pane.getParent.getScene.window())
    }
  }

  override def receive: Receive = ???

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    pane.children = List(forgeDirButton, meshDirButton, outDirButton, experimentDirButton)
  }
}
