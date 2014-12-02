package io

object ConcreteRun extends Application {
  def main(args: Array[String]): Unit = {
    val args = Seq(1184.7946319480084, -0.0027421042068676016, -0.05725313697907164, 0.13530930301949828, -0.030766536851669263, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    function.fitness(args)
  }
}
