package util.jfx

import javafx.collections.ObservableList
import javafx.scene.{ Group, Node }
import javafx.scene.layout.{ Region, BorderPane, Pane }

import scalafx.scene.layout.StackPane

object SFXUtil {

  def getXShift(descendant: Node, ancestor: Node): Double = {
    var ret: Double = 0.0
    var curr: Node = descendant
    while (curr ne ancestor) {
      ret += curr.getLocalToParentTransform.getTx
      curr = curr.getParent
      if (curr == null) throw new IllegalArgumentException("'descendant' Node is not a descendant of 'ancestor")
    }
    ret
  }

  def getYShift(descendant: Node, ancestor: Node): Double = {
    var ret: Double = 0.0
    var curr: Node = descendant
    while (curr ne ancestor) {
      ret += curr.getLocalToParentTransform.getTy
      curr = curr.getParent
      if (curr == null) throw new IllegalArgumentException("'descendant' Node is not a descendant of 'ancestor")
    }
    ret
  }

  def replaceComponent(original: Node, replacement: Node) {
    val parent: Pane = original.getParent.asInstanceOf[Pane]
    replacement.getProperties.putAll(original.getProperties)
    original.getProperties.clear()
    val children: ObservableList[Node] = parent.getChildren
    val originalIndex: Int = children.indexOf(original)
    parent match {
      case borderPane: BorderPane =>
        if (borderPane.getTop eq original) {
          children.remove(original)
          borderPane.setTop(replacement)
        } else if (borderPane.getLeft eq original) {
          children.remove(original)
          borderPane.setLeft(replacement)
        } else if (borderPane.getCenter eq original) {
          children.remove(original)
          borderPane.setCenter(replacement)
        } else if (borderPane.getRight eq original) {
          children.remove(original)
          borderPane.setRight(replacement)
        } else if (borderPane.getBottom eq original) {
          children.remove(original)
          borderPane.setBottom(replacement)
        }
      case _ =>
        children.set(originalIndex, replacement)
    }
  }

  def createScalePane(region: Region, w: Double, h: Double, `override`: Boolean): StackPane = {
    val nw = if (`override` || region.getPrefWidth == Region.USE_COMPUTED_SIZE) {
      region.setPrefWidth(w)
      w
    } else region.getPrefWidth

    val nh = if (`override` || region.getPrefHeight == Region.USE_COMPUTED_SIZE) {
      region.setPrefHeight(h)
      h
    } else region.getPrefHeight
    val ret: StackPane = new StackPane
    ret.setPrefWidth(nw)
    ret.setPrefHeight(nh)
    if (region.getParent != null) replaceComponent(region, ret)
    val group: Group = new Group(region)
    ret.getChildren.add(group)
    group.scaleXProperty.bind(ret.widthProperty.divide(nw))
    group.scaleYProperty.bind(ret.heightProperty.divide(nh))
    ret
  }
}