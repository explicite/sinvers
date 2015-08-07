package ui.controls

import javafx.beans.value
import javafx.beans.value.ChangeListener

import akka.actor.{ Props, Actor, ActorLogging }
import db.service.FullInversService
import db.{ DbConnection, FullInversId }
import ui.Protocol.{ Absent, Hide, Present, Show }
import ui.controls.FullInversChart.SetFullInvers
import ui.view.FullInversView
import util.Util
import util.Util.scienceLowFormatter

import scala.util.{ Failure, Success, Try }
import scalafx.Includes._
import scalafx.beans.property.ObjectProperty
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.control.TableColumn._
import scalafx.scene.control._
import scalafx.scene.image.{ Image, ImageView }
import scalafx.scene.layout.VBox
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

class FullInversList extends Actor with ActorLogging with DbConnection {

  def simulations = ObservableBuffer(FullInversService.findAll())

  val list = new TableView[FullInversView] {
    columns += (
      new TableColumn[FullInversView, Long] {
        text = "id"
        cellValueFactory = { features => ObjectProperty[Long](features.value.id.value) }
        editable = false
      },
      new TableColumn[FullInversView, String] {
        text = "args(a1,m1..m9,epss)"
        cellValueFactory = { features =>
          ObjectProperty[String](
            s"${scienceLowFormatter(features.value.a1)}, ${scienceLowFormatter(features.value.m1)},${scienceLowFormatter(features.value.m2)},${scienceLowFormatter(features.value.m3)},${scienceLowFormatter(features.value.m4)},${scienceLowFormatter(features.value.m5)},${scienceLowFormatter(features.value.m6)},${scienceLowFormatter(features.value.m8)},${scienceLowFormatter(features.value.m9)},${scienceLowFormatter(features.value.epsSs)}"
          )
        }
        editable = false
        prefWidth = 350
      },
      new TableColumn[FullInversView, String] {
        text = "score"
        cellValueFactory = { features => ObjectProperty[String](s"${scienceLowFormatter(features.value.score)}") }
        editable = false
      },
      new TableColumn[FullInversView, FullInversId] {
        text = "open"
        cellValueFactory = { features => ObjectProperty[FullInversId](features.value.id) }
        cellFactory = { tableColumn =>
          val cell = new TableCell[FullInversView, FullInversId]()
          val button: Button = new Button {
            text = "open"
            onAction = (ae: ActionEvent) => {
              context.actorOf(Props[FullInversChart]) ! SetFullInvers(cell.getItem)
            }
          }
          cell.itemProperty().addListener(new ChangeListener[FullInversId] {
            override def changed(observable: value.ObservableValue[_ <: FullInversId], oldValue: FullInversId, newValue: FullInversId): Unit = {
              if (newValue == null) {
                cell.setGraphic(null)
              } else {
                cell.setGraphic(button)
              }
            }
          })
          cell
        }
      },
      new TableColumn[FullInversView, FullInversView] {
        text = "export"
        cellValueFactory = { features => ObjectProperty[FullInversView](features.value) }
        cellFactory = { tableColumn =>
          val cell = new TableCell[FullInversView, FullInversView]()
          val button: Button = new Button {
            text = "export"
            onAction = (ae: ActionEvent) => {
              val fileChooser = new FileChooser() {
                title = "Save result"
                selectedExtensionFilter = new ExtensionFilter("TXT files (*.txt)", "*.txt")
              }
              Try(fileChooser.showSaveDialog(cell.getParent.getParent.getScene.getWindow)) match {
                case Success(file) =>
                  val text = cell.getItem.txt
                  Util.write(file.toPath, text.getBytes)
                case Failure(err) => log.error(err.getMessage)
              }
              refresh()
            }
          }
          cell.itemProperty().addListener(new ChangeListener[FullInversView] {
            override def changed(observable: value.ObservableValue[_ <: FullInversView], oldValue: FullInversView, newValue: FullInversView): Unit = {
              if (newValue == null) {
                cell.setGraphic(null)
              } else {
                cell.setGraphic(button)
              }
            }
          })
          cell.setContentDisplay(ContentDisplay.GraphicOnly)
          cell
        }
      },
      new TableColumn[FullInversView, FullInversId] {
        text = "delete"
        cellValueFactory = { features => ObjectProperty[FullInversId](features.value.id) }
        cellFactory = { tableColumn =>
          val cell = new TableCell[FullInversView, FullInversId]()
          val button: Button = new Button {
            onAction = (ae: ActionEvent) => {
              FullInversService.deleteById(cell.itemProperty().get())
              refresh()
            }
            graphic = new ImageView {
              image = new Image(getClass.getResourceAsStream("/css/icon_close_alt.png"))
            }
          }
          cell.itemProperty().addListener(new ChangeListener[FullInversId] {
            override def changed(observable: value.ObservableValue[_ <: FullInversId], oldValue: FullInversId, newValue: FullInversId): Unit = {
              if (newValue == null) {
                cell.setGraphic(null)
              } else {
                cell.setGraphic(button)
              }
            }
          })
          cell.setContentDisplay(ContentDisplay.GraphicOnly)
          cell
        }
      }
    )
  }

  val panel = new VBox {
    children = List(list)
  }

  private def refresh(): Unit = {
    list.items = simulations
  }

  override def receive: Receive = {
    case Present =>
      refresh()
      sender() ! Show(panel)
    case Absent =>
      sender() ! Hide(panel)
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    list.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)
  }
}
