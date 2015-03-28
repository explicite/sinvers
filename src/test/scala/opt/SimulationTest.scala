package opt

import java.nio.file.Paths

import data.{KGF, DataContainer}
import test.BaseTest

class SimulationTest extends BaseTest {
  behavior of "application"

  it should "optimize" in {
    val fx2Dir = Paths.get("C:\\Users\\Jan\\Desktop\\Forge2-V3.0")
    val source = Paths.get("C:\\Users\\Jan\\Desktop\\sym")

    val steering = source.resolve("pilotage.dat")
    val mesh = source.resolve("work.may")
    val out = source.resolve("file.out")
    val experimentDirectory = "C:\\Users\\Jan\\Desktop\\mgr\\HA000490.D01"

    val experiment = DataContainer(new java.io.File(experimentDirectory))
    val function = FitnessFunction(fx2Dir, mesh, out, 1000, system, experiment, KGF)
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
