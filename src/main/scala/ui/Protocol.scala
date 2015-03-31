package ui

import scalafx.scene.Node

object Protocol {

  case class Show(node: Node)

  case class Close(node: Node)

  case object Reset

  case object Present

  case object Absent

  case class Register(node: Node, column: Int, row: Int)

  case class Unregister(node: Node)

  case class Iteration(fitness: Double, stamp: Long)

}
