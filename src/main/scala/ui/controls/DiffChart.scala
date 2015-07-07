package ui.controls

import scalafx.Includes._
import akka.actor.{ Props, Actor, ActorLogging }
import data.{ KGF, ResultContainer, DataContainer }
import ui.Protocol.{ Absent, Present, Hide, Show }
import ui.controls.DiffChart.AddData
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.chart.{ LineChart, NumberAxis, XYChart }
import scalafx.scene.control.Button
import scalafx.scene.layout.HBox

class DiffChart extends Actor with ActorLogging {

  private val xAxis = new NumberAxis {
    label = "Jaw[mm]"
  }

  private val yAxis = new NumberAxis {
    label = "Force[MPa]"
  }

  private val series = ObservableBuffer.empty[javafx.scene.chart.XYChart.Series[Number, Number]]

  private val chart = new LineChart[Number, Number](xAxis, yAxis, series) {
    minHeight = 800
    minWidth = 1024
    createSymbols = false
    animated = false
    stylesheets add "css/data-chart.css"
  }

  val addButton = new Button {
    text = "add"
    onAction = (ae: ActionEvent) => {
      context.system.actorOf(Props[DiffConfigurator]) ! Present
      context become addNew
    }
  }

  val panel = new HBox {
    padding = Insets(10)
    children = List(chart, addButton)
  }

  override def receive: Receive = ready

  def ready: Receive = {
    case Present =>
      sender() ! Show(panel)
    case Absent =>
      sender() ! Hide(panel)
  }

  def addNew: Receive = {
    case AddData(o, c, tile) =>
      val orgSeries = new XYChart.Series[Number, Number] { name = s"org {$tile}" }
      val coputedSeries = new XYChart.Series[Number, Number] { name = s"opt {$tile}" }
      val orgData = o.jaw.zip(o.force)
      orgSeries.data = orgData.map { case (x, y) => XYChart.Data[Number, Number](-1d * x, -1d * y) }
      series.add(orgSeries)
      val compData = c.jaw.map(_ - 12d).zip(c.force.map(_ * KGF.conversion))
      coputedSeries.data = compData.map { case (x, y) => XYChart.Data[Number, Number](-1d * x, -1d * y) }
      series.add(coputedSeries)
      chart.data = series

  }

}

object DiffChart {

  case class AddData(org: DataContainer, computed: ResultContainer, tail: String)

}