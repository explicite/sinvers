package ui.controls

import akka.actor.{ ActorLogging, Actor }
import db.{ InversId, DbConnection }
import db.service.InversService
import opt.HSFunction
import ui.Protocol.Show
import ui.controls.InversChart.SetInvers

import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{ XYChart, LineChart, NumberAxis }

class InversChart extends Actor with ActorLogging with DbConnection {
  private val gui = context.system.actorSelection("akka://sinvers/user/gui")

  private val xAxis = new NumberAxis {}

  private val yAxis = new NumberAxis {}

  private var chart: LineChart[Number, Number] = null

  override def receive: Receive = {
    case SetInvers(inversId) =>
      val inversView = InversService.findById(inversId)
      val strains = (1 to 100).map(_ / 100d)
      val inversFunction = HSFunction(inversView)
      val data = strains.zip(strains.map(inversFunction.apply)).map { case (x, y) => XYChart.Data[Number, Number](x, y) }
      val series = new XYChart.Series[Number, Number] {}
      series.data = data
      chart = new LineChart[Number, Number](xAxis, yAxis, ObservableBuffer(series)) {
        minHeight = 800
        minWidth = 1024
        createSymbols = false
        animated = false
      }

      gui ! Show(chart)
  }
}

object InversChart {

  case class SetInvers(inversId: InversId)

}