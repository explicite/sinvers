package ui.controls

import akka.actor.{ Actor, ActorLogging, Props }
import io.FullInversSimulation
import io.Protocol.OptimizeFullInvers
import opt.StaticInterval
import ui.Protocol.{ Absent, Hide, Present, Show }
import ui.controls.FullInversProtocol.InversList
import ui.controls.InversChooser.Open
import ui.view.InversView

import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control.{ Button, TextField }
import scalafx.scene.layout.{ HBox, VBox }

class FullInversConfigurator extends Actor with ActorLogging {
  val configurator = new HBox() {
    padding = Insets(20)
    spacing = 10
  }

  val okButton = new Button {
    text = "ok"
    onAction = (ae: ActionEvent) => {
      context.actorOf(Props[InversChooser]) ! Open
      context become waitingForInversList
    }
  }

  val cancelButton = new Button {
    id = "cancel"
    text = "cancel"
    onAction = (ae: ActionEvent) => {
      context.parent ! Hide(configurator)
    }
  }

  val decision = new HBox {
    children = List(okButton, cancelButton)
  }

  //hs arguments
  val a1MinInput = new TextField {
    promptText = "a1 min"
    text = "100"
  }

  val a1MaxInput = new TextField {
    promptText = "a1 max"
    text = "250"
  }

  val m1MinInput = new TextField {
    promptText = "m1 min"
    text = "-2.0E-3"
  }

  val m1MaxInput = new TextField {
    promptText = "m1 max"
    text = "-3.0E-3"
  }

  val m2MinInput = new TextField {
    promptText = "m2 min"
    text = "2.0E-1"
  }

  val m2MaxInput = new TextField {
    promptText = "m2 max"
    text = "3.0E-1"
  }

  val m3MinInput = new TextField {
    promptText = "m3 min"
    text = "2.0E-2"
  }

  val m3MaxInput = new TextField {
    promptText = "m3 max"
    text = "3.0E-2"
  }

  val m4MinInput = new TextField {
    promptText = "m4 min"
    text = "4.0E-3"
  }

  val m4MaxInput = new TextField {
    promptText = "m4 max"
    text = "-5.0E-3"
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
  }

  val noIterations = new TextField {
    promptText = "iterations"
  }

  val leftColumn = new VBox {
    children = List(noWolfs, noIterations, decision)
  }

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
      case (min, max) => new HBox {
        children = List(min, max)
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

  def waitingForInversList: Receive = {
    case InversList(args) =>
      val bounds = hsList.map {
        case (min, max) =>
          StaticInterval(min.text.value.toDouble, max.text.value.toDouble)
      }
      context.system.actorOf(Props[FullInversSimulation]) ! OptimizeFullInvers(
        args,
        bounds,
        noWolfs.text.value.toInt,
        noIterations.text.value.toInt
      )
      context.parent ! Hide(configurator)
      context become ready
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = configurator.children = List(leftColumn, rightColumn)

}

object FullInversProtocol {

  case class InversList(args: Seq[InversView])

}

