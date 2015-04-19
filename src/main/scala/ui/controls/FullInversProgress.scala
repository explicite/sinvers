package ui.controls

import akka.actor.{ Actor, ActorLogging }
import db.service.FullInversService
import db.{ DbConnection, InversId }
import reo.HSArgs
import ui.Protocol.{ Add, Iteration, Remove, Reset }
import ui.controls.FullInversProgress.SetEnd

import scala.language.postfixOps
import scala.math.abs
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.{ Insets, Pos }
import scalafx.scene.control.{ Button, ProgressBar }
import scalafx.scene.image.{ Image, ImageView }
import scalafx.scene.layout.{ HBox, VBox }

class FullInversProgress extends Actor with ActorLogging with DbConnection {
  implicit val executionContext = context.system.dispatchers.lookup("scalafx-dispatcher")

  import ui.controls.ProgressProtocol.SetStart

  val gui = context.actorSelection("akka://sinvers/user/gui")

  val progressBar = new ProgressBar() {
    prefWidth = 280
    minWidth = 280
    maxWidth = 280
    progress = 0
    stylesheets add "css/progress-bar.css"
  }

  var args: Option[(Seq[InversId], HSArgs)] = None

  val progress = new VBox {
    prefWidth = 300
    minWidth = 300
    maxWidth = 300
    padding = Insets(20)
    spacing = 20
    style = "" +
      "-fx-background-color: white;" +
      "-fx-border-color: red;" +
      "-fx-border-width: 1;" +
      "-fx-border-radius: 6;" +
      "-fx-padding: 6;"
  }

  val saveButton = new Button {
    onAction = (ae: ActionEvent) => {
      args.foreach {
        case (inversIds, arg) =>
          println(args)
          FullInversService.save(inversIds, arg)
          gui ! Remove(progress)
      }
    }
    graphic = new ImageView {
      image = new Image(getClass.getResourceAsStream("/css/icon_check_alt.png"))
    }
    disable = true
  }

  val removeButton = new Button {
    onAction = (ae: ActionEvent) => {
      args.foreach {
        arg => gui ! Remove(progress)
      }
    }
    graphic = new ImageView {
      image = new Image(getClass.getResourceAsStream("/css/icon_close_alt.png"))
    }
    disable = true
  }

  val box = new HBox {
    alignment = Pos.TopRight
    children = List(saveButton, removeButton)
  }

  def receive = toSet

  def set(start: Long, max: Double): Receive = {
    case Iteration(_, stamp) =>
      progressBar.synchronized {
        progressBar.setProgress(progressBar.progress.value + 1 / max)
      }

    case SetEnd(inversIds, newArgs) =>
      saveButton.disable = false
      removeButton.disable = false
      args = Some(inversIds, newArgs)
    case Reset =>
      gui ! Remove(progress)
      progressBar.setProgress(0)
      context become toSet
  }

  def toSet: Receive = {
    case SetStart(start, max) =>
      gui ! Add(progress)
      context become set(start, max)
  }

  def formatter(d: Double): String = new java.text.DecimalFormat("0.###").format(abs(d))

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = progress.children = Seq(progressBar, box)

}

object FullInversProgress {

  case class SetEnd(inversIds: Seq[InversId], arg: HSArgs)

}
