package ui.controls

import akka.actor.{ Actor, ActorLogging, Props }
import ui.Protocol._

import scala.collection.mutable
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{ Menu, MenuBar, MenuItem }
import scalafx.scene.layout.{ FlowPane, BorderPane, GridPane }
import scalafx.stage.Stage

class GUI extends Actor with ActorLogging {
  val progress = context.actorOf(Props[Progress], "progress")
  val chart = context.actorOf(Props[FitnessChart], "fitness-chart")
  val simulation = context.actorOf(Props[SimulationConfigurator], "simulation")

  val stages = mutable.Set.empty[Stage]

  val menuBar = new MenuBar {
    menus = List(
      new Menu("simulation") {
        items = List(
          new MenuItem("new") {
            onAction = {
              e: ActionEvent => simulation ! Present
            }
          },
          new MenuItem("list") {
            onAction = {
              e: ActionEvent => println("list")
            }
          },
          new MenuItem("load") {
            onAction = {
              e: ActionEvent => println("load")
            }
          },
          new MenuItem("save") {
            onAction = {
              e: ActionEvent => println("save")
            }
          }
        )
      }
    )
  }

  val pane = new GridPane {
    prefWidth = 1024
    prefHeight = 640
    padding = Insets(18)
  }

  val manePane = new BorderPane {
    top = menuBar
    center = pane
  }

  val stage = new Stage {
    title = "sinvers"
    width = 864
    height = 550
    scene = new Scene {
      root = manePane
    }
    onCloseRequest = handle {
      System.exit(0)
    }
  }

  override def receive: Receive = {
    case Register(node, column, row) =>
      pane.add(node, column, row)
    case Unregister(node) =>
      pane.getChildren.remove(node)
    case Show(node) =>
      val newStage = new Stage {
        scene = new Scene {
          root = new FlowPane {children = node}
        }
        onCloseRequest = handle {self ! Close(node)}
      }
      stages.add(newStage)
      newStage.show()
    case Close(node) =>
      stages.find(_.scene.get().getChildren.contains(node)).foreach {
        toClose =>
          toClose.close()
          stages.remove(toClose)
      }
    case iteration: Iteration =>
      progress ! iteration
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = stage.show()
}
