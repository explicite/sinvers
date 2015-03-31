package ui.controls

import java.io.File

import akka.actor.{ Actor, ActorLogging }
import ui.Protocol.{ Absent, Close, Present, Show }

import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox
import scalafx.stage.FileChooser

class SimulationConfigurator extends Actor with ActorLogging {
  val configurator = new VBox() {
    padding = Insets(20)
    spacing = 10
  }

  var forge: File = null
  val forgeDirButton = new Button {
    id = "forgeDirButton"
    text = "forge directory"
    onAction = (ae: ActionEvent) => {
      val fileChooser = new FileChooser() {
        title = "Pick a forge dir"
      }
      forge = fileChooser.showOpenDialog(configurator.getParent.getScene.window())
    }
  }

  var mesh: File = null
  val meshDirButton = new Button {
    id = "meshDirButton"
    text = "mesh file"
    onAction = (ae: ActionEvent) => {
      val fileChooser = new FileChooser() {
        title = "Pick a mesh dir"
      }
      mesh = fileChooser.showOpenDialog(configurator.getParent.getScene.window())
    }
  }

  var out: File = null
  val outDirButton = new Button {
    id = "outDirButton"
    text = "out file"
    onAction = (ae: ActionEvent) => {
      val fileChooser = new FileChooser() {
        title = "Pick a out dir"
      }
      out = fileChooser.showOpenDialog(configurator.getParent.getScene.window())
    }
  }

  var experiment: File = null
  val experimentDirButton = new Button {
    id = "experimentDirButton"
    text = "experiment file"
    onAction = (ae: ActionEvent) => {
      val fileChooser = new FileChooser() {
        title = "Pick a experiment dir"
      }
      experiment = fileChooser.showOpenDialog(configurator.getParent.getScene.window())
    }
  }

  override def receive: Receive = {
    case Present =>
      sender() ! Show(configurator)
    case Absent =>
      sender() ! Close(configurator)
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = configurator.children = List(forgeDirButton, meshDirButton, outDirButton, experimentDirButton)

}
