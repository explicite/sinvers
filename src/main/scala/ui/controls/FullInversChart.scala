package ui.controls

import akka.actor.{ Actor, ActorLogging }
import db.service.FullInversService
import db.{ FullInversId, DbConnection }
import opt.HSFunction
import ui.Protocol.Show
import ui.controls.FullInversChart.SetFullInvers
import ui.view.{ InversView, FullInversView }

import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.XYChart.Series
import scalafx.scene.chart.{ LineChart, XYChart, NumberAxis }

class FullInversChart extends Actor with ActorLogging with DbConnection {
  private val gui = context.system.actorSelection("akka://sinvers/user/gui")

  private val xAxis = new NumberAxis {}

  private val yAxis = new NumberAxis {}

  private var chart: LineChart[Number, Number] = null

  override def receive: Receive = {
    case SetFullInvers(fullInversId) =>
      val fullInversView = FullInversService.findById(fullInversId)
      val strains = (1 to 100).map(_ / 100d)
      val inversFunctions = fullInversView.inverses.map(HSFunction)
      val FullInversView(id, _, _, a1, m1, m2, m3, m4, m5, m6, m7, m8, m9, epsSs) = fullInversView
      val optimizedFunctions = fullInversView.inverses.map {
        inversView =>
          val optimizedInvers = InversView(inversView.id, inversView.temperature, inversView.strainRate, inversView.score, a1, m1, m2, m3, m4, m5, m6, m7, m8, m9, epsSs)
          HSFunction(optimizedInvers)
      }

      val inversFunctionsWithTag = inversFunctions.map(fun => (fun, "org"))
      val optimizedFunctionWithTag = optimizedFunctions.map(fun => (fun, "fi"))
      val functions = inversFunctionsWithTag ++: optimizedFunctionWithTag

      val data = functions.map {
        case (function, tag) => (strains.zip(strains.map(function.apply)).map { case (x, y) => XYChart.Data[Number, Number](x, y) }, function.args.temperature, function.args.strainRate, tag)
      }

      val series = data.map {
        case (sx, tmp, str, tag) =>
          Series.sfxXYChartSeries2jfx(
            new XYChart.Series[Number, Number] {
              data = sx
              name = s"$tag {tmp:$tmp, str:$str}"
            }
          )
      }

      chart = new LineChart[Number, Number](xAxis, yAxis, ObservableBuffer(series)) {
        minHeight = 800
        minWidth = 1024
        createSymbols = false
        animated = false
      }

      gui ! Show(chart)
  }

}

object FullInversChart {
  case class SetFullInvers(fullInversId: FullInversId)
}
