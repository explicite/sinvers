package io

object ConcreteRun extends Application {
  def main(args: Array[String]): Unit = {
    val args = Seq(1428.8877295802831, -0.002837017001469392, -0.07841975839691163, 0.10979229032210779, -0.04307499531592205, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    function.fitness(args)
  }
}
