package ui.controls

import akka.actor.{ Actor, ActorLogging }
import ui.Protocol._

import scalafx.collections.ObservableBuffer
import scalafx.scene.chart._

class FitnessChart extends Actor with ActorLogging {

  val xAxis = new NumberAxis {
    label = "iteration"
    forceZeroInRange = false
  }

  private val yAxis = new NumberAxis {
    label = "fitness"
  }

  private val series = new XYChart.Series[Number, Number] {}

  private val chart = new LineChart[Number, Number](xAxis, yAxis, ObservableBuffer(series)) {
    title = "Fitness monitoring for GWO"
    maxWidth = 750
    legendVisible = false
    animated = false
  }

  def receive = toIncrement

  def toIncrement: Receive = {
    case Iteration(fitness, _) =>
      if (Double.MaxValue > fitness) {
        series.getData.add(XYChart.Data[Number, Number](0, fitness))
      }
      context.parent ! Show(chart)
      context become incremented(1)
  }

  def incremented(iteration: Int): Receive = {
    case Iteration(fitness, _) =>
      if (Double.MaxValue > fitness) {
        series.getData.add(XYChart.Data[Number, Number](iteration, fitness))
        if (series.getData.size() > 15) series.getData.remove(0)
      }
      context become incremented(iteration + 1)

    case Reset =>
      context.parent ! Hide(chart)
      context become toIncrement
  }

}
