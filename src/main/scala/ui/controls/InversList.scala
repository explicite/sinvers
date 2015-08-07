package ui.controls

import javafx.beans.value
import javafx.beans.value.ChangeListener

import akka.actor.{ Props, Actor, ActorLogging }
import db.service.InversService
import db.{ DbConnection, InversId }
import ui.Protocol.{ Absent, Hide, Present, Show }
import ui.controls.InversChart.SetInvers
import ui.view.InversView
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

class InversList extends Actor with ActorLogging with DbConnection {

  def simulations = ObservableBuffer(InversService.findAll())

  val list = new TableView[InversView] {
    columns += (
      new TableColumn[InversView, Long] {
        text = "id"
        cellValueFactory = { features => ObjectProperty[Long](features.value.id.value) }
        editable = false
      },
      new TableColumn[InversView, Double] {
        text = "tmp[ºC]"
        cellValueFactory = { features => ObjectProperty[Double](features.value.temperature) }
        editable = false
      },
      new TableColumn[InversView, Double] {
        text = "SR[s-1]"
        cellValueFactory = { features => ObjectProperty[Double](features.value.strainRate) }
        editable = false
      },
      new TableColumn[InversView, String] {
        text = "score"
        cellValueFactory = { features => ObjectProperty[String](s"${scienceLowFormatter(features.value.score)}") }
        editable = false
      },
      new TableColumn[InversView, String] {
        text = "args(a1,m1..m9,epss)"
        cellValueFactory = { features =>
          ObjectProperty[String](
            s"${scienceLowFormatter(features.value.a1)}, ${scienceLowFormatter(features.value.m1)},${scienceLowFormatter(features.value.m2)},${scienceLowFormatter(features.value.m3)},${scienceLowFormatter(features.value.m4)},${scienceLowFormatter(features.value.m5)},${scienceLowFormatter(features.value.m6)},${scienceLowFormatter(features.value.m8)},${scienceLowFormatter(features.value.m9)},${scienceLowFormatter(features.value.epsSs)}"
          )
        }
        editable = false
        prefWidth = 350
      }, new TableColumn[InversView, InversId] {
        text = "open"
        cellValueFactory = { features => ObjectProperty[InversId](features.value.id) }
        cellFactory = { tableColumn =>
          val cell = new TableCell[InversView, InversId]()
          val button: Button = new Button {
            text = "open"
            onAction = (ae: ActionEvent) => {
              context.actorOf(Props[InversChart]) ! SetInvers(cell.getItem)
            }
          }
          cell.itemProperty().addListener(new ChangeListener[InversId] {
            override def changed(observable: value.ObservableValue[_ <: InversId], oldValue: InversId, newValue: InversId): Unit = {
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
      new TableColumn[InversView, InversView] {
        text = "export"
        cellValueFactory = { features => ObjectProperty[InversView](features.value) }
        cellFactory = { tableColumn =>
          val cell = new TableCell[InversView, InversView]()
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
          cell.itemProperty().addListener(new ChangeListener[InversView] {
            override def changed(observable: value.ObservableValue[_ <: InversView], oldValue: InversView, newValue: InversView): Unit = {
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
      new TableColumn[InversView, InversId] {
        text = "delete"
        cellValueFactory = { features => ObjectProperty[InversId](features.value.id) }
        cellFactory = { tableColumn =>
          val cell = new TableCell[InversView, InversId]()
          val button: Button = new Button {
            onAction = (ae: ActionEvent) => {
              InversService.deleteById(cell.itemProperty().get())
              refresh()
            }
            graphic = new ImageView {
              image = new Image(getClass.getResourceAsStream("/css/icon_close_alt.png"))
            }
          }
          cell.itemProperty().addListener(new ChangeListener[InversId] {
            override def changed(observable: value.ObservableValue[_ <: InversId], oldValue: InversId, newValue: InversId): Unit = {
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
