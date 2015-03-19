package opt

import java.nio.file.Paths

import data.DataFile
import test.BaseTest

class SimulationTest extends BaseTest {
  behavior of "application"

  it should "optimize" in {
    val fx2Dir = Paths.get("C:\\Users\\Jan\\Desktop\\Forge2-V3.0")
    val source = Paths.get("C:\\Users\\Jan\\Desktop\\sym")

    val experimentDirectory = "C:\\Users\\Jan\\Desktop\\mgr\\HA000490.D01"

    val experiment = DataFile(new java.io.File(experimentDirectory), temperature = 1000, steering = "pilotage.dat")
    val function = FitnessFunction(fx2Dir, source, system, experiment)
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

    min should not be empty
  }

}
