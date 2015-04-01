package ui.controls

import java.nio.file.Paths

import akka.actor.{ Props, Actor, ActorLogging }
import io.Protocol.Optimize
import io.Simulation
import ui.Protocol.{ Absent, Close, Present, Show }

import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control.{ Button, TextField }
import scalafx.scene.layout.{ HBox, VBox }
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

class SimulationConfigurator extends Actor with ActorLogging {
  val configurator = new VBox() {
    padding = Insets(20)
    spacing = 10
  }

  val forgeInput = new TextField {
    promptText = "forge"
  }

  val forge = {
    val forgeDirButton = new Button {
      id = "forgeDirButton"
      text = "forge exe"
      onAction = (ae: ActionEvent) => {
        val fileChooser = new FileChooser() {
          title = "Pick a forge exe"
          selectedExtensionFilter = new ExtensionFilter("Exe", "*.exe")
        }
        forgeInput.text = fileChooser.showOpenDialog(configurator.getParent.getScene.window()).getPath
      }
    }
    new HBox { children = List(forgeInput, forgeDirButton) }
  }

  val meshInput = new TextField {
    promptText = "mesh"
  }

  val mesh = {
    val meshDirButton = new Button {
      id = "meshDirButton"
      text = "mesh file"
      onAction = (ae: ActionEvent) => {
        val fileChooser = new FileChooser() {
          title = "Pick a mesh file"
          selectedExtensionFilter = new ExtensionFilter("Mesh", "*.may")
        }
        meshInput.text = fileChooser.showOpenDialog(configurator.getParent.getScene.window()).getPath
      }
    }
    new HBox { children = List(meshInput, meshDirButton) }
  }

  val outInput = new TextField {
    promptText = "out"
  }

  val out = {
    val outDirButton = new Button {
      id = "outDirButton"
      text = "out file"
      onAction = (ae: ActionEvent) => {
        val fileChooser = new FileChooser() {
          title = "Pick a out file"
          selectedExtensionFilter = new ExtensionFilter("Out", "*.out")
        }
        outInput.text = fileChooser.showOpenDialog(configurator.getParent.getScene.window()).getPath
      }
    }
    new HBox { children = List(outInput, outDirButton) }
  }

  val experimentInput = new TextField {
    promptText = "experiment"
  }

  val experiment = {
    val experimentDirButton = new Button {
      id = "experimentDirButton"
      text = "experiment file"
      onAction = (ae: ActionEvent) => {
        val fileChooser = new FileChooser() { title = "Pick a experiment dir" }
        experimentInput.text = fileChooser.showOpenDialog(configurator.getParent.getScene.window()).getPath
      }
    }
    new HBox { children = List(experimentInput, experimentDirButton) }
  }

  val temperature = new TextField { promptText = "temperature" }

  val okButton = new Button {
    id = "ok"
    text = "ok"
    onAction = (ae: ActionEvent) => {
      context.system.actorOf(Props[Simulation]) ! Optimize(
        Paths.get(forgeInput.text.value),
        Paths.get(meshInput.text.value),
        Paths.get(outInput.text.value),
        Paths.get(experimentInput.text.value),
        temperature.text.value.toDouble
      )
      context.parent ! Close(configurator)
    }
  }

  val cancelButton = new Button {
    id = "cancel"
    text = "cancel"
    onAction = (ae: ActionEvent) => {
      forgeInput.text = null
      meshInput.text = null
      outInput.text = null
      experimentInput.text = null
      temperature.text = null
      context.parent ! Close(configurator)
    }
  }

  val decision = new HBox { children = List(okButton, cancelButton) }

  override def receive: Receive = {
    case Present =>
      sender() ! Show(configurator)
    case Absent =>
      sender() ! Close(configurator)
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = configurator.children = List(forge, mesh, out, experiment, temperature, decision)

}
