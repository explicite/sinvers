package opt

import data.DataFile
import io.{DON, Forge}
import test.BaseTest

class SimulationTest  extends BaseTest {
  behavior of "application"

  it should "optimize" in {
    val fx2Dir = "C:\\Users\\Jan\\Desktop\\Forge2-V3.0"
    val workingDirectory = "C:\\Users\\Jan\\Desktop\\sym"
    val process = Forge(fx2Dir)

    val experimentDirectory = "C:\\Users\\Jan\\Desktop\\mgr\\HA000490.D01"
    val don = DON(new java.io.File(s"$workingDirectory\\newSym.don"))
    val experimentData = DataFile(new java.io.File(experimentDirectory))
    val function = InversFunction(process, don, experimentData)
    val bounds = Seq(
      StaticInterval(862908.976, 862908.976), //a1 ok
      StaticInterval(-0.00092, -0.00092), //m1 ok
      StaticInterval(0.1291, 0.1291), //m2 ok
      StaticInterval(0.1283, 0.1283), //m3 ok
      StaticInterval(-0.00977, -0.00977), //m4 ok
      StaticInterval(0, 0), //m5
      StaticInterval(0, 0), //m6
      StaticInterval(0, 0), //m7
      StaticInterval(0, 0), //m8 ok
      StaticInterval(0, 0), //m9
      StaticInterval(0, 0) //epSS
    )
    val optimizer = GreyWolfOptimizer(function.fitness, bounds)

    val min = optimizer.min(50, 20)

    println(min)

    min._1 should not be empty
  }

}
