package io

import data.DataFile
import opt._


object Application {

  def main(args: Array[String]) = {
    val fx2Dir = "C:\\Users\\Jan\\Desktop\\Forge2-V3.0"
    val workingDirectory = "C:\\Users\\Jan\\Desktop\\sym"
    val process = Forge(fx2Dir)

    val experimentDirectory = "C:\\Users\\Jan\\Desktop\\mgr\\HA000490.D01"
    val don = DON(new java.io.File(s"$workingDirectory\\newSym.don"))
    val experimentData = DataFile(new java.io.File(experimentDirectory))
    val function = InversFunction(process, don, experimentData)
    val bounds = Seq(
      IntervalWithTolerance(607.0044998611718, 0.1), //a1 ok
      IntervalWithTolerance(-0.006566258245714811, 1), //m1 ok
      IntervalWithTolerance(-0.21007539044798862, 1), //m2 ok
      IntervalWithTolerance(-0.2076373049421277, 1), //m3 ok
      IntervalWithTolerance(-0.046202182847251284, 1), //m4 ok
      IntervalWithTolerance(1.3470572457687486E-4, 1), //m5
      StaticInterval(0.0), //m6
      IntervalWithTolerance(-0.15230128443911398, 1), //m7
      IntervalWithTolerance(1.9711823426304953E-4, 1), //m8 ok
      IntervalWithTolerance(0.6570079207003687, 1), //m9
      StaticInterval(0.0) //epSS
    )

    def product(xs: Seq[Double], ys: Seq[Double]) = {
      /*def index(i: Int): Int = {
        ((i % xs.size) + xs.size) % xs.size
      }
      xs.zip(ys).zipWithIndex.map {
        case ((x, y), i) =>
          (x * ys(index(i + 1))) - (xs(index(i + 1)) * y)
      }*/

      xs.zip(ys) map {
        case (x, y) => x * y
      }
    }
    val points = Seq(
      Seq(607.0044998611718, -0.006566258245714811, -0.21007539044798862, -0.2076373049421277, -0.046202182847251284),
      Seq(1232986.0, -0.00254, -0.05621, 0.145, 0.0324),
      Seq(8967.7883774088, -0.0027157465970086836, -0.5555030531125177, 0.14696533787487387, -0.03524000837297703),
      Seq(8391.038944327514, -0.0029205021897638073, -0.2474891475591282, 0.19531189181363873, -0.04314211922812594),
      Seq(27390.728451939907, -0.7487866628746606, 0.39396568529787546, -0.5147254573787851, -0.49498682650852716),
      Seq(10038.923674945858, -0.0026738415315356034, -0.29736891320678477, 0.16118954869013166, -0.04495610282313123),
      Seq(9781.516103555536, -0.0025526373389388457, -0.3622779419730735, 0.15639967311141878, -0.040893254263988044)
    )
    val nelderMeadOptimizer = NelderMead(function.fitness)
    val min = nelderMeadOptimizer.minimize(points, 10)

    /*val optimizer = GreyWolfOptimizer(function.fitness, bounds)
    val min = optimizer.min(20, 30)*/

    println(min)
  }
}
