package ui.controls

import javafx.event.EventHandler
import javafx.scene.input.{ MouseButton, MouseEvent }

import akka.actor.{ Actor, ActorLogging }
import data.DataContainer
import opt.StaticInterval
import ui.Protocol.Show
import ui.controls.DataChart.SetData
import util.jfxutils.SFXChartUtil
import util.jfxutils.chart.ChartPanManager

import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.chart.{ LineChart, NumberAxis, XYChart }
import scalafx.scene.control.{ Button, TextField }
import scalafx.scene.layout.{ VBox, HBox }

class DataChart extends Actor with ActorLogging {
  private val gui = context.system.actorSelection("akka://sinvers/user/gui")

  private val xAxis = new NumberAxis {}

  private val yAxis = new NumberAxis {}

  private val series = new XYChart.Series[Number, Number] {}

  private val chart = new LineChart[Number, Number](xAxis, yAxis, ObservableBuffer(series)) {
    minHeight = 800
    minWidth = 1024
    legendVisible = false
    createSymbols = false
    animated = false
  }

  private val panner = new ChartPanManager(chart)

  override def receive: Receive = {
    case SetData(data) =>
      series.data = null
      val chartData = data.jaw.zip(data.force)
      series.data = chartData.map { case (x, y) => XYChart.Data[Number, Number](x, y) }

      val min = new TextField {
        promptText = "min"
      }

      val max = new TextField {
        promptText = "max"
      }

      val sliceButton = new Button {
        id = "sliceButton"
        text = "slice"
        onAction = (ae: ActionEvent) => {
          val interval = StaticInterval(min.text.value.toDouble, max.text.value.toDouble)
          val slice = data.slice(interval)
          val dataSlice = slice.jaw.zip(slice.force)
          series.data = dataSlice.map { case (x, y) => XYChart.Data[Number, Number](x, y) }
        }
      }

      val sliceBox = new VBox {
        children = List(min, max, sliceButton)
      }

      val chartPanel = SFXChartUtil.setupZooming(chart, new EventHandler[MouseEvent]() {
        override def handle(event: MouseEvent): Unit = {
          if (event.getButton != MouseButton.PRIMARY || event.isShortcutDown) event.consume()
        }
      })

      gui ! Show(new HBox {
        padding = Insets(10)
        children = List(chartPanel, sliceBox)
      })
  }

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

object DataChart {

  case class SetData(data: DataContainer)

}