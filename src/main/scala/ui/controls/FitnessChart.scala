package ui.controls

import akka.actor.{ Actor, ActorLogging }
import ui.controls.FitnessChartProtocol.Iteration

import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.chart._
import scalafx.scene.layout.BorderPane
import scalafx.stage.Stage

class FitnessChart extends Actor with ActorLogging {

  private val xAxis = new NumberAxis {
    label = "iteration"
  }

  private val yAxis = new NumberAxis {
    label = "fitness"
  }

  private val alpha = new XYChart.Series[Number, Number] {
    name = "alpha"
  }

  private val beta = new XYChart.Series[Number, Number] {
    name = "beta"
  }

  private val delta = new XYChart.Series[Number, Number] {
    name = "delta"
  }

  private val chart = new LineChart[Number, Number](xAxis, yAxis, ObservableBuffer(alpha, beta, delta)) {
    title = "Fitness monitoring for GWO"
  }

  val stage = {
    val s = new Stage {
      outer =>
      title = "Fitness"
      scene = new Scene {
        root = new BorderPane {
          padding = Insets(25)
          bottom = chart
        }
      }
    }
    s.show()
    s
  }

  def receive = {
    case Iteration(a, b, d, iteration) =>

      if (Double.MaxValue > a) {
        alpha.getData.add(XYChart.Data[Number, Number](iteration, a))
        beta.getData.add(XYChart.Data[Number, Number](iteration, b))
        delta.getData.add(XYChart.Data[Number, Number](iteration, d))
      }
  }
}

object FitnessChartProtocol {

  case class Iteration(alpha: Double, beta: Double, delta: Double, iteration: Int)

}
