package ui.controls

import akka.actor.{ Actor, ActorLogging }

import scalafx.collections.ObservableBuffer
import scalafx.scene.chart._

class FitnessChart extends Actor with ActorLogging {

  private val xAxis = new NumberAxis {
    label = "iteration"
  }

  private val yAxis = new NumberAxis {
    label = "fitness"
  }

  private val alpha = new XYChart.Series[Number, Number] {
    name = "alpha wolf"
  }

  private val chart = new LineChart[Number, Number](xAxis, yAxis, ObservableBuffer(alpha)) {
    title = "Fitness monitoring for GWO"
  }

  import FitnessChartProtocol._

  def receive = toIncrement

  def toIncrement: Receive = {
    case Iteration(fitness: Double) =>
      if (Double.MaxValue > fitness) {
        alpha.getData.add(XYChart.Data[Number, Number](0, fitness))
      }
      context become incremented(1)
  }

  def incremented(iteration: Int): Receive = {
    case Iteration(fitness) =>
      if (Double.MaxValue > fitness) {
        alpha.getData.add(XYChart.Data[Number, Number](iteration, fitness))
      }
      context become incremented(iteration + 1)

    case Reset => context become toIncrement
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    GUI.pane.setCenter(chart)
  }

}

object FitnessChartProtocol {

  case class Iteration(fitness: Double)

  case object Reset

}
