package util.jfx

import javafx.event.EventHandler
import javafx.scene.chart.XYChart
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.{ Rectangle, StrokeType }

import util.jfx.chart.ChartZoomManager

import scalafx.scene.Node
import scalafx.scene.layout.StackPane

object SFXChartUtil {

  def setupZooming(chart: XYChart[_, _]): Node = {
    setupZooming(chart, ChartZoomManager.DEFAULT_FILTER)
  }

  def setupZooming(chart: XYChart[_, _], mouseFilter: EventHandler[_ >: MouseEvent]): Node = {
    val chartPane: StackPane = new StackPane
    if (chart.getParent != null) SFXUtil.replaceComponent(chart, chartPane)
    val selectRect: Rectangle = new Rectangle(0, 0, 0, 0)
    selectRect.setFill(Color.DODGERBLUE)
    selectRect.setMouseTransparent(true)
    selectRect.setOpacity(0.3)
    selectRect.setStroke(Color.rgb(0, 0x29, 0x66))
    selectRect.setStrokeType(StrokeType.INSIDE)
    selectRect.setStrokeWidth(3.0)
    chartPane.getChildren.addAll(chart, selectRect)
    val zoomManager: ChartZoomManager = new ChartZoomManager(chartPane, selectRect, chart)
    zoomManager.setMouseFilter(mouseFilter)
    zoomManager.start()
    chartPane
  }

}

