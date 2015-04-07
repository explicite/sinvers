package ui

import scalafx.scene.Node

object Protocol {

  case class Show(node: Node)

  case class Hide(node: Node)

  case object Reset

  case object Present

  case object Absent

  case class Iteration(fitness: Double, stamp: Long)

  /**
   * Adding to presentation queue
   * @param node node to present in queue
   */
  case class Add(node: Node)

  /**
   * Remove from presentation queue
   * @param node node to remove from queue
   */
  case class Remove(node: Node)

}
