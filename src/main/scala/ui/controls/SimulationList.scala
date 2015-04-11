package ui.controls

import akka.actor.{ Actor, ActorLogging }
import db.DbConnection
import db.service.SimulationService
import ui.Protocol.{ Absent, Hide, Present, Show }
import ui.view.SimulationView
import util.Util.scienceLowFormatter

import scalafx.beans.property.ObjectProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.TableColumn._
import scalafx.scene.control.{ TableColumn, TableView }

class SimulationList extends Actor with ActorLogging with DbConnection {

  def simulations = ObservableBuffer(SimulationService.findAll())

  val list = new TableView[SimulationView] {
    columns ++= List(
      new TableColumn[SimulationView, Long] {
        text = "id"
        cellValueFactory = { features => ObjectProperty[Long](features.value.id.value) }
        editable = false
      },
      new TableColumn[SimulationView, Double] {
        text = "temp"
        cellValueFactory = { features => ObjectProperty[Double](features.value.temperature) }
        editable = false
      },
      new TableColumn[SimulationView, String] {
        text = "args(m1...m9,epss)"
        cellValueFactory = { features =>
          ObjectProperty[String](
            s"${scienceLowFormatter(features.value.m1)},${scienceLowFormatter(features.value.m2)},${scienceLowFormatter(features.value.m3)},${scienceLowFormatter(features.value.m4)},${scienceLowFormatter(features.value.m5)},${scienceLowFormatter(features.value.m6)},${scienceLowFormatter(features.value.m8)},${scienceLowFormatter(features.value.m9)},${scienceLowFormatter(features.value.epsSs)}"
          )
        }
        editable = false
        prefWidth = 350
      }
    )
  }

  override def receive: Receive = {
    case Present =>
      list.items = simulations
      sender() ! Show(list)
    case Absent =>
      sender() ! Hide(list)
  }

}
