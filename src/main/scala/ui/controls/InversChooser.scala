package ui.controls

import javafx.beans.value
import javafx.beans.value.ChangeListener

import akka.actor.{ Actor, ActorLogging }
import db.InversId
import db.service.InversService
import ui.Protocol.{ Hide, Show }
import ui.controls.FullInversProtocol.InversList
import ui.controls.InversChooser.Open
import ui.view.InversView
import util.Util.scienceLowFormatter

import scalafx.Includes._
import scalafx.beans.property.ObjectProperty
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.control.TableColumn._
import scalafx.scene.control._
import scalafx.scene.image.{ Image, ImageView }
import scalafx.scene.layout.VBox

class InversChooser extends Actor with ActorLogging {
  private val gui = context.system.actorSelection("akka://sinvers/user/gui")

  def simulations = ObservableBuffer(InversService.findAll())

  val list = new TableView[InversView] {
    columns += (
      new TableColumn[InversView, Long] {
        text = "id"
        cellValueFactory = { features => ObjectProperty[Long](features.value.id.value) }
        editable = false
      },
      new TableColumn[InversView, Double] {
        text = "tmp[�C]"
        cellValueFactory = { features => ObjectProperty[Double](features.value.temperature) }
        editable = false
      },
      new TableColumn[InversView, Double] {
        text = "SR[s-1]"
        cellValueFactory = { features => ObjectProperty[Double](features.value.strainRate) }
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

  val okButton = new Button {
    text = "Ok"
    onAction = (ae: ActionEvent) => {
      context.parent ! InversList(list.getSelectionModel.getSelectedItems)
      hide()
    }
  }

  val panel = new VBox {
    children = List(list, okButton)
  }

  def hide(): Unit = gui ! Hide(panel)

  private def refresh(): Unit = {
    list.items = simulations
  }

  override def receive: Receive = {
    case Open =>
      refresh()
      gui ! Show(panel)
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    list.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)
  }

}

object InversChooser {

  case object Open

}