package ui.controls

import javafx.event.EventHandler
import javafx.scene.input.{ MouseButton, MouseEvent }

import akka.actor.{ Actor, ActorLogging }
import data.{ KGF, ResultContainer, DataContainer }
import ui.Protocol.{ Hide, Show }
import ui.controls.DiffChart.SetData
import util.jfx.SFXChartUtil
import util.jfx.chart.ChartPanManager

import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.chart.{ LineChart, NumberAxis, XYChart }
import scalafx.scene.layout.HBox

class DiffChart extends Actor with ActorLogging {
  private val gui = context.system.actorSelection("akka://sinvers/user/gui")

  private val xAxis = new NumberAxis {}

  private val yAxis = new NumberAxis {}

  private val orgSeries = new XYChart.Series[Number, Number] { name = "org" }

  private val coputedSeries = new XYChart.Series[Number, Number] { name = "comp" }

  private val chart = new LineChart[Number, Number](xAxis, yAxis, ObservableBuffer(orgSeries, coputedSeries)) {
    minHeight = 800
    minWidth = 1024
    createSymbols = false
    animated = false
    stylesheets add "css/data-chart.css"
  }

  private val panner = new ChartPanManager(chart)

  private var org: DataContainer = null
  private val comp: ResultContainer = null

  val chartPanel = SFXChartUtil.setupZooming(chart, new EventHandler[MouseEvent]() {
    override def handle(event: MouseEvent): Unit = {
      if (event.getButton != MouseButton.PRIMARY || event.isShortcutDown) event.consume()
    }
  })

  val panel = new HBox {
    padding = Insets(10)
    children = List(chartPanel)
  }

  override def receive: Receive = {
    case SetData(o, c) =>
      val orgData = o.jaw.zip(o.force)
      orgSeries.data = orgData.map { case (x, y) => XYChart.Data[Number, Number](x, y) }
      val compData = c.jaw.map(_ - 12d).zip(c.force.map(_ * KGF.conversion))
      coputedSeries.data = compData.map { case (x, y) => XYChart.Data[Number, Number](x, y) }
      gui ! Show(panel)
  }

  def clean(): Unit = gui ! Hide(panel)

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    panner.setMouseFilter(new EventHandler[MouseEvent]() {
      override def handle(event: MouseEvent): Unit = {
        if (event.getButton == MouseButton.SECONDARY ||
          (event.getButton == MouseButton.PRIMARY &&
            event.isShortcutDown)) {} else event.consume()
      }
    })
    panner.start()
  }

}

object DiffChart {

  case class SetData(org: DataContainer, computed: ResultContainer)

}