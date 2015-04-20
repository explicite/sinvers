package ui.controls

import akka.actor.{ ActorLogging, Actor }
import db.{ Configuration => DbConfiguration, Configurations }
import ui.Protocol.{ Absent, Show, Present, Hide }

import scala.util.{ Failure, Success, Try }
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.control.{ Button, TextField }
import scalafx.scene.layout.{ HBox, VBox }
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.Includes._
import db.repository.ConfigurationRepository

class Configuration extends Actor with ActorLogging {

  val configurator = new VBox() {
    padding = Insets(20)
    spacing = 10
  }

  val forgeInput = new TextField { promptText = Configurations.forge }

  val forge = {
    val forgeDirButton = new Button {
      text = "forge exe"
      onAction = (ae: ActionEvent) => {
        val fileChooser = new FileChooser() {
          title = "Pick a forge exe"
          selectedExtensionFilter = new ExtensionFilter("Exe", "*.exe")
        }
        Try(fileChooser.showOpenDialog(configurator.getParent.getScene.window()).getPath) match {
          case Success(path) => forgeInput.text = path
          case Failure(err)  => log.error(err.getMessage)
        }
      }
    }
    new HBox { children = List(forgeInput, forgeDirButton) }
  }

  val saveButton = new Button {
    text = "save"
    onAction = (ae: ActionEvent) => {
      ConfigurationRepository.save(DbConfiguration(Configurations.forge, forgeInput.text.value))
      context.parent ! Hide(configurator)
    }
  }

  val cancelButton = new Button {
    id = "cancel"
    text = "cancel"
    onAction = (ae: ActionEvent) => { context.parent ! Hide(configurator) }
  }

  val decision = new HBox { children = List(saveButton, cancelButton) }

  private def refresh() = {
    ConfigurationRepository.findByKey(Configurations.forge).foreach(conf => forgeInput.text = conf.value)
  }

  override def receive: Receive = {
    case Present =>
      refresh()
      sender() ! Show(configurator)
    case Absent =>
      sender() ! Hide(configurator)
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = configurator.children = List(forge, decision)

}
