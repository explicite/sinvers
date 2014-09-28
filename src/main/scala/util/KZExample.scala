package util

import scalax.chart.module.Charting
import scalax.chart.XYChart

object KZExample extends Charting {
  def main(args: Array[String]) = {
    val x: List[Double] = List.fill(20)(0.0) ::: List.tabulate(300)(n => scala.math.sin((n / 300.0) * 8.0 * scala.math.Pi)) ::: List.fill(20)(0.0)
    val r = new XORShiftRandom
    //trashed sin values
    val tx: List[Double] = x.map {
      x => x + ((r.nextDouble() - 0.5) * 0.4)
    }

    val chartData = Seq((0, 0)).toXYSeriesCollection("default")
    chartData.removeAllSeries()

    chartData.addSeries((for (i <- 0 until x.length) yield (i, x(i))).toXYSeries("clean sin"))
    chartData.addSeries((for (i <- 0 until tx.length) yield (i, tx(i))).toXYSeries("trashed sin"))
    chartData.addSeries((for (i <- 0 until tx.length) yield (i, KZ(tx, 3, 20)(i))).toXYSeries("KZ sin"))

    val chart: XYChart = XYLineChart(chartData)
    chart.show()
  }

}
