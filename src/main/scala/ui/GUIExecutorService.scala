package ui

import java.util.Collections
import java.util.concurrent.{ AbstractExecutorService, TimeUnit }
import javafx.embed.swing.JFXPanel

import scalafx.application.Platform

abstract class GUIExecutorService extends AbstractExecutorService {
  def execute(command: Runnable): Unit

  def shutdown(): Unit = ()

  def shutdownNow() = Collections.emptyList[Runnable]

  def isShutdown = false

  def isTerminated = false

  def awaitTermination(l: Long, timeUnit: TimeUnit) = true
}

object ScalaFXExecutorService extends GUIExecutorService {
  //trick to initialize fx toolkit
  new JFXPanel()
  override def execute(command: Runnable) = Platform.runLater(command)
}