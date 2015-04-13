package ui.controls

import java.nio.file.{ Path, Paths }

import akka.actor.{ Props, Actor, ActorLogging }
import data.{ Samples, DataContainer }
import db.Configurations
import db.repository.ConfigurationRepository
import io.Protocol.Optimize
import io.Simulation
import ui.Protocol.{ Absent, Hide, Present, Show }
import ui.controls.DataChart.SetData
import ui.controls.Protocol.Slice

import scala.util.{ Failure, Success, Try }
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control.{ ComboBox, Button, TextField }
import scalafx.scene.layout.{ HBox, VBox }
import scalafx.stage.FileChooser

class SimulationConfigurator extends Actor with ActorLogging {

  val configurator = new VBox() {
    padding = Insets(20)
    spacing = 10
  }

  val samples = new ComboBox[Samples] {
    promptText = "samples"
    items = ObservableBuffer(Samples.TenToTwelve)
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
        Paths.get(ConfigurationRepository.findByKey(Configurations.forge).get.value),
        samples.getSelectionModel.getSelectedItem,
        slice,
        temperature.text.value.toDouble,
        strainRate.text.value.toDouble
      )
      cleanup()
      context.parent ! Hide(configurator)
      context become ready
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = configurator.children = List(experiment, temperature, strainRate, samples, decision)

  private def cleanup(): Unit = {
    experimentInput.text = null
    temperature.text = null
    strainRate.text = null
  }

}

object Protocol {
  case class Slice(dataContainer: DataContainer)
}