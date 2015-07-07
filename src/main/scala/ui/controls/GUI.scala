package ui.controls

import akka.actor.{ Actor, ActorLogging, Props }
import db.Repositories
import ui.Protocol._

import scala.collection.mutable
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.{ Node, Scene }
import scalafx.scene.control.{ Menu, MenuBar, MenuItem }
import scalafx.scene.layout.{ BorderPane, FlowPane }
import scalafx.stage.Stage

class GUI extends Actor with ActorLogging {
  val chart = context.actorOf(Props[FitnessChart], "fitness-chart")
  val inversConfigurator = context.actorOf(Props[InversConfigurator], "invers-configurator")
  val fullInversConfigurator = context.actorOf(Props[FullInversConfigurator], "full-invers-configurator")
  val fullInversList = context.actorOf(Props[FullInversList], "full-invers-list")
  val inversList = context.actorOf(Props[InversList], "invers-list")
  val diffChart = context.actorOf(Props[DiffChart].withDispatcher("scalafx-dispatcher"), "diff-chart")
  val configuration = context.actorOf(Props[Configuration], "configuration")

  val stages = mutable.Set.empty[Stage]

  val menuBar = new MenuBar {
    menus = List(
      new Menu("invers") {
        items = List(
          new MenuItem("new       ") {
            onAction = { e: ActionEvent => inversConfigurator ! Present }
          },
          new MenuItem("list") {
            onAction = { e: ActionEvent => inversList ! Present }
          }
        )
      }, new Menu("full invers") {
        items = List(
          new MenuItem("new       ") {
            onAction = { e: ActionEvent => fullInversConfigurator ! Present }
          },
          new MenuItem("list") {
            onAction = { e: ActionEvent => fullInversList ! Present }
          }
        )
      },
      new Menu("diff") {
        items = List(
          new MenuItem("new       ") {
            onAction = { e: ActionEvent => diffChart ! Present }
          }
        )
      },
      new Menu("conf") {
        items = List(
          new MenuItem("edit") {
            onAction = { e: ActionEvent => configuration ! Present }
          }
        )
      }
    )
  }

  val pane = new FlowPane() {
    padding = Insets(18)
    hgap = 3
  }

  val manePane = new BorderPane {
    top = menuBar
    center = pane
  }

  val stage = new Stage {
    title = "sinvers"
    width = 864
    height = 550
    scene = new Scene { root = manePane }
    onCloseRequest = handle { System.exit(0) }
    resizable = true
  }

  override def receive: Receive = {
    case Show(node)   => show(node)
    case Hide(node)   => hide(node)
    case Add(node)    => add(node)
    case Remove(node) => remove(node)
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    Repositories.createSchema()
    stage.show()
  }

  private def add(node: Node) = pane.children.add(node)

  private def remove(node: Node) = pane.children.remove(node)

  private def hide(node: Node) = {
    stages.find(_.scene.get().getChildren.contains(node)).foreach {
      toClose =>
        toClose.close()
        stages.remove(toClose)
    }
  }

  private def show(node: Node) = {
    val newStage = new Stage {
      scene = new Scene {
        root = new FlowPane { children = node }
      }
      onCloseRequest = handle {
        self ! Hide(node)
      }
    }
    stages.add(newStage)
    newStage.show()
  }

}
