package ui.controls

import java.nio.file.{ Path, Paths }

import akka.actor.{ Props, Actor, ActorLogging }
import data.DataContainer
import io.Protocol.Optimize
import io.Simulation
import ui.Protocol.{ Absent, Hide, Present, Show }
import ui.controls.DataChart.SetData
import ui.controls.Protocol.Slice

import scala.util.{ Failure, Success, Try }
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
      text = "forge exe"
      onAction = (ae: ActionEvent) => {
        val fileChooser = new FileChooser() {
          title = "Pick a forge exe"
          selectedExtensionFilter = new ExtensionFilter("Exe", "*.exe")
        }
        Try(fileChooser.showOpenDialog(configurator.getParent.getScene.window()).getPath) match {
          case Success(path) => forgeInput.text = path
          case Failure(err)  => log.error(err.getMessage)
        }
      }
    }
    new HBox { children = List(forgeInput, forgeDirButton) }
  }

  val meshInput = new TextField {
    promptText = "mesh"
  }

  val mesh = {
    val meshDirButton = new Button {
      text = "mesh file"
      onAction = (ae: ActionEvent) => {
        val fileChooser = new FileChooser() {
          title = "Pick a mesh file"
          selectedExtensionFilter = new ExtensionFilter("Mesh", "*.may")
        }
        Try(fileChooser.showOpenDialog(configurator.getParent.getScene.window()).getPath) match {
          case Success(path) => meshInput.text = path
          case Failure(err)  => log.error(err.getMessage)
        }
      }
    }
    new HBox { children = List(meshInput, meshDirButton) }
  }

  val outInput = new TextField {
    promptText = "out"
  }

  val out = {
    val outDirButton = new Button {
      text = "out file"
      onAction = (ae: ActionEvent) => {
        val fileChooser = new FileChooser() {
          title = "Pick a out file"
          selectedExtensionFilter = new ExtensionFilter("Out", "*.out")
        }
        Try(fileChooser.showOpenDialog(configurator.getParent.getScene.window()).getPath) match {
          case Success(path) => outInput.text = path
          case Failure(err)  => log.error(err.getMessage)
        }
      }
    }
    new HBox { children = List(outInput, outDirButton) }
  }

  val experimentInput = new TextField {
    promptText = "experiment"
  }

  val experiment = {
    val experimentDirButton = new Button {
      text = "experiment file"
      onAction = (ae: ActionEvent) => {
        val fileChooser = new FileChooser() { title = "Pick a experiment dir" }
        Try(fileChooser.showOpenDialog(configurator.getParent.getScene.window()).getPath) match {
          case Success(path) => experimentInput.text = path
          case Failure(err)  => log.error(err.getMessage)
        }
      }
    }
    new HBox { children = List(experimentInput, experimentDirButton) }
  }

  val temperature = new TextField { promptText = "temperature" }

  val strainRate = new TextField { promptText = "strain rate" }

  val okButton = new Button {
    text = "ok"
    onAction = (ae: ActionEvent) => {
      val dataContainer = DataContainer(Paths.get(experimentInput.text.value).toFile)
      context.actorOf(Props[DataChart]) ! SetData(dataContainer)
      context become waitingForSlice
    }
  }

  val cancelButton = new Button {
    id = "cancel"
    text = "cancel"
    onAction = (ae: ActionEvent) => {
      cleanup()
      context.parent ! Hide(configurator)
    }
  }

  val decision = new HBox { children = List(okButton, cancelButton) }

  override def receive = ready

  def ready: Receive = {
    case Present =>
      sender() ! Show(configurator)
    case Absent =>
      sender() ! Hide(configurator)
  }

  def waitingForSlice: Receive = {
    case Slice(slice) =>
      context.system.actorOf(Props[Simulation]) ! Optimize(
        Paths.get(forgeInput.text.value),
        Paths.get(meshInput.text.value),
        Paths.get(outInput.text.value),
        slice,
        temperature.text.value.toDouble,
        strainRate.text.value.toDouble
      )
      cleanup()
      context.parent ! Hide(configurator)
      context become ready
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = configurator.children = List(forge, mesh, out, experiment, temperature, strainRate, decision)

  private def cleanup(): Unit = {
    forgeInput.text = null
    meshInput.text = null
    outInput.text = null
    experimentInput.text = null
    temperature.text = null
    strainRate.text = null
  }

}

object Protocol {
  case class Files(forge: Path, mesh: Path, out: Path, experiment: Path)
  case class Slice(dataContainer: DataContainer)
}