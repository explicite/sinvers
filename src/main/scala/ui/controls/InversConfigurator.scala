package ui.controls

import java.nio.file.Paths

import akka.actor.{ Props, Actor, ActorLogging }
import data.{ Samples, DataContainer }
import db.Configurations
import db.repository.ConfigurationRepository
import io.Protocol.OptimizeInvers
import io.InversSimulation
import opt.StaticInterval
import ui.Protocol.{ Absent, Hide, Present, Show }
import ui.controls.DataChart.SetData
import ui.controls.InversProtocol.Slice

import scala.util.{ Failure, Success, Try }
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control.{ ComboBox, Button, TextField }
import scalafx.scene.layout.{ HBox, VBox }
import scalafx.stage.FileChooser

class InversConfigurator extends Actor with ActorLogging {

  val configurator = new HBox() {
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
      Try(DataContainer(Paths.get(experimentInput.text.value).toFile)) match {
        case Success(dataContainer) =>
          context.actorOf(Props[DataChart]) ! SetData(dataContainer)
          context become waitingForSlice
        case Failure(err) => log.error(err.getMessage)
      }
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

  //hs arguments
  val a1MinInput = new TextField {
    promptText = "a1 min"
    text = "1000"
  }

  val a1MaxInput = new TextField {
    promptText = "a1 max"
    text = "4000"
  }

  val m1MinInput = new TextField {
    promptText = "m1 min"
    text = "-0.5"
  }

  val m1MaxInput = new TextField {
    promptText = "m1 max"
    text = "0.5"
  }

  val m2MinInput = new TextField {
    promptText = "m2 min"
    text = "-0.5"
  }

  val m2MaxInput = new TextField {
    promptText = "m2 max"
    text = "0.5"
  }

  val m3MinInput = new TextField {
    promptText = "m3 min"
    text = "-0.5"
  }

  val m3MaxInput = new TextField {
    promptText = "m3 max"
    text = "0.5"
  }

  val m4MinInput = new TextField {
    promptText = "m4 min"
    text = "-0.5"
  }

  val m4MaxInput = new TextField {
    promptText = "m4 max"
    text = "0.5"
  }

  val m5MinInput = new TextField {
    promptText = "m5 min"
    text = "0"
  }

  val m5MaxInput = new TextField {
    promptText = "m5 max"
    text = "0"
  }

  val m6MinInput = new TextField {
    promptText = "m6 min"
    text = "0"
  }

  val m6MaxInput = new TextField {
    promptText = "m6 max"
    text = "0"
  }

  val m7MinInput = new TextField {
    promptText = "m7 min"
    text = "0"
  }

  val m7MaxInput = new TextField {
    promptText = "m7 max"
    text = "0"
  }

  val m8MinInput = new TextField {
    promptText = "m8 min"
    text = "0"
  }

  val m8MaxInput = new TextField {
    promptText = "m8 max"
    text = "0"
  }

  val m9MinInput = new TextField {
    promptText = "m9 min"
    text = "0"
  }

  val m9MaxInput = new TextField {
    promptText = "m9 max"
    text = "0"
  }

  val epSSMinInput = new TextField {
    promptText = "eps-ss min"
    text = "0"
  }

  val epSSMaxInput = new TextField {
    promptText = "eps-ss max"
    text = "0"
  }

  val noWolfs = new TextField {
    promptText = "wolfs"
    text = "200"
  }

  val noIterations = new TextField {
    promptText = "iterations"
    text = "200"
  }

  val leftColumn = new VBox { children = List(experiment, temperature, strainRate, noWolfs, noIterations, samples, decision) }

  val hsList = List(
    (a1MinInput, a1MaxInput),
    (m1MinInput, m1MaxInput),
    (m2MinInput, m2MaxInput),
    (m3MinInput, m3MaxInput),
    (m4MinInput, m4MaxInput),
    (m5MinInput, m5MaxInput),
    (m6MinInput, m6MaxInput),
    (m7MinInput, m7MaxInput),
    (m8MinInput, m8MaxInput),
    (m9MinInput, m9MaxInput),
    (epSSMinInput, epSSMaxInput)
  )

  val rightColumn = new VBox {
    children = hsList.map {
      case (min, max) => new HBox { children = List(min, max) }
    }
  }

  override def receive = ready

  def ready: Receive = {
    case Present =>
      sender() ! Show(configurator)
    case Absent =>
      sender() ! Hide(configurator)
  }

  def waitingForSlice: Receive = {
    case Slice(slice) =>
      val hsParameters = hsList.map {
        case (min, max) =>
          StaticInterval(min.text.value.toDouble, max.text.value.toDouble)
      }
      context.system.actorOf(Props[InversSimulation]) ! OptimizeInvers(
        Paths.get(ConfigurationRepository.findByKey(Configurations.forge).get.value),
        samples.getSelectionModel.getSelectedItem,
        slice,
        temperature.text.value.toDouble,
        strainRate.text.value.toDouble,
        hsParameters,
        noWolfs.text.value.toInt,
        noIterations.text.value.toInt
      )
      cleanup()
      context.parent ! Hide(configurator)
      context become ready
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = configurator.children = List(leftColumn, rightColumn)

  private def cleanup(): Unit = {
    experimentInput.text = null
    temperature.text = null
    strainRate.text = null
  }

}

object InversProtocol {
  case class Slice(dataContainer: DataContainer)
}