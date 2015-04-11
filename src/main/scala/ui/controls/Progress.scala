package ui.controls

import java.time.Duration

import akka.actor.{ Actor, ActorLogging }
import db.{ Simulation, HSArgument, DbConnection }
import db.repository.{ SimulationRepository, HSArgumentRepository }
import reo.HSArgs
import ui.Protocol._

import scala.language.postfixOps
import scala.math.abs
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.{ Insets, Pos }
import scalafx.scene.control.{ Button, ProgressBar }
import scalafx.scene.image.{ Image, ImageView }
import scalafx.scene.layout.{ HBox, VBox }
import scalafx.scene.text.Text

class Progress extends Actor with ActorLogging with DbConnection {

  import ui.controls.ProgressProtocol._

  val gui = context.actorSelection("akka://sinvers/user/gui")

  val progressBar = new ProgressBar() {
    prefWidth = 280
    minWidth = 280
    maxWidth = 280
    progress = 0
    stylesheets add "css/progress-bar.css"
  }

  var args: Option[(Double, Double, HSArgs)] = None

  val eta = new Text {
    text = s"ETA: ? s perf: 0 it/s"
  }

  val progress = new VBox {
    prefWidth = 300
    minWidth = 300
    maxWidth = 300
    padding = Insets(20)
    spacing = 20
    style = "" +
      "-fx-background-color: white;" +
      "-fx-border-color: black;" +
      "-fx-border-width: 1;" +
      "-fx-border-radius: 6;" +
      "-fx-padding: 6;"
  }

  val saveButton = new Button {
    onAction = (ae: ActionEvent) => {
      args.foreach {
        case (temperature, strainRate, arg) =>
          val id = HSArgumentRepository.save(HSArgument(None, arg.a1, arg.m1, arg.m2, arg.m3, arg.m4, arg.m5, arg.m6, arg.m7, arg.m8, arg.m9, arg.epsSs))
          SimulationRepository.save(Simulation(None, id, temperature, strainRate))
          println(SimulationRepository.findAll())

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
      progressBar.setProgress(progressBar.progress.value + 1 / max)
      eta.text = {
        val iterations = max * progressBar.progress.value
        val performance = ((stamp - start) / 1e9) / iterations
        val duration = Duration.ofSeconds(((max - iterations) * performance).toLong)
        s"ETA: $duration s perf: ${formatter(1 / performance)} it/s"
      }
    case SetEnd(temperature, strainRate, newArgs) =>
      saveButton.disable = false
      removeButton.disable = false
      args = Some(temperature, strainRate, newArgs)
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
  override def preStart(): Unit = progress.children = Seq(progressBar, eta, box)

}

object ProgressProtocol {

  case class SetStart(start: Long, max: Double)

  case class SetEnd(temperature: Double, strainRate: Double, hsArgs: HSArgs)

}
