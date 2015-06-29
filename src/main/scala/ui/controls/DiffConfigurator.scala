package ui.controls

import data.{ ResultContainer, Samples, DataContainer }
import io.forge.Protocol.Job
import io.forge.{ Protocol, Worker }
import reo.HSArgs

import java.nio.file.Paths

import akka.actor.{ Props, Actor, ActorLogging }
import db.Configurations
import db.repository.ConfigurationRepository
import opt.StaticInterval
import ui.Protocol.{ Absent, Hide, Present, Show }
import ui.controls.DataChart.{ SetData => SetChart }
import ui.controls.DiffChart.{ SetData => SetDiff }
import ui.controls.InversProtocol.Slice

import scala.util.{ Failure, Success, Try }
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control.{ ComboBox, Button, TextField }
import scalafx.scene.layout.{ HBox, VBox }
import scalafx.stage.FileChooser

class DiffConfigurator extends Actor with ActorLogging {
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
        val fileChooser = new FileChooser() {
          title = "Pick a experiment dir"
        }
        Try(fileChooser.showOpenDialog(configurator.getParent.getScene.window()).getPath) match {
          case Success(path) => experimentInput.text = path
          case Failure(err)  => log.error(err.getMessage)
        }
      }
    }
    new HBox {
      children = List(experimentInput, experimentDirButton)
    }
  }

  val temperature = new TextField {
    promptText = "temperature"
  }

  val strainRate = new TextField {
    promptText = "strain rate"
  }

  val okButton = new Button {
    text = "ok"
    onAction = (ae: ActionEvent) => {
      Try(DataContainer(Paths.get(experimentInput.text.value).toFile)) match {
        case Success(dataContainer) =>
          context.actorOf(Props[DataChart]) ! SetChart(dataContainer)
          context become waitingForSlice
        case Failure(err) => log.error(err.getMessage)
      }
    }
  }

  def waitingForSlice: Receive = {
    case Slice(slice) =>
      val sample = Samples.TenToTwelve
      val interval = {
        val max = slice.jaw.max + sample.max
        val min = slice.jaw.min + sample.max
        StaticInterval(min, max)
      }
      val interpolatorInterval = {
        StaticInterval(slice.jaw.min - 0.01, slice.jaw.max + 0.01)
      }

      val preparedData = slice.slice(interpolatorInterval)
      val steering = preparedData.steering(sample.max)
      val hsArgs = HSArgs(hsList.map(_.text.value.toDouble))
      context.system.actorOf(Props[Worker]) ! Job(
        Paths.get(ConfigurationRepository.findByKey(Configurations.forge).get.value),
        Protocol.Parameters(
          sample,
          steering,
          interval,
          temperature.text.value.toDouble,
          hsArgs
        )
      )

      context become waitForResult(slice)
  }

  def waitForResult(slice: DataContainer): Receive = {
    case result: ResultContainer =>
      context.system.actorOf(Props[DiffChart]) ! SetDiff(
        slice,
        result
      )
  }

  val cancelButton = new Button {
    id = "cancel"
    text = "cancel"
    onAction = (ae: ActionEvent) => {
      cleanup()
      context.parent ! Hide(configurator)
    }
  }

  val decision = new HBox {
    children = List(okButton, cancelButton)
  }

  //hs arguments
  val a1Input = new TextField {
    promptText = "a1"
    text = "100"
  }

  val m1Input = new TextField {
    promptText = "m1"
    text = "-2.0E-3"
  }

  val m2Input = new TextField {
    promptText = "m2"
    text = "2.0E-1"
  }

  val m3Input = new TextField {
    promptText = "m3"
    text = "2.0E-2"
  }

  val m4Input = new TextField {
    promptText = "m4"
    text = "4.0E-3"
  }

  val m5Input = new TextField {
    promptText = "m5"
    text = "0"
  }

  val m6Input = new TextField {
    promptText = "m6"
    text = "0"
  }

  val m7Input = new TextField {
    promptText = "m7"
    text = "0"
  }

  val m8Input = new TextField {
    promptText = "m8"
    text = "0"
  }

  val m9Input = new TextField {
    promptText = "m9"
    text = "0"
  }

  val epSSInput = new TextField {
    promptText = "eps-ss"
    text = "0"
  }

  val leftColumn = new VBox {
    children = List(experiment, temperature, strainRate, samples, decision)
  }

  val hsList = List(
    a1Input,
    m1Input,
    m2Input,
    m3Input,
    m4Input,
    m5Input,
    m6Input,
    m7Input,
    m8Input,
    m9Input,
    epSSInput
  )

  val rightColumn = new VBox {
    children = hsList.map {
      case value => new HBox {
        children = List(value)
      }
    }
  }

  override def receive = ready

  def ready: Receive = {
    case Present =>
      sender() ! Show(configurator)
    case Absent =>
      sender() ! Hide(configurator)
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = configurator.children = List(leftColumn, rightColumn)

  private def cleanup(): Unit = {
    experimentInput.text = null
    temperature.text = null
    strainRate.text = null
  }

}
