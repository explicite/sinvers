package ui

import akka.actor.{ Actor, ActorLogging }

import scala.compat.Platform
import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.BorderPane
import scalafx.stage.Stage

class DONConfigurator extends Actor with ActorLogging {

  val stage = new Stage {
    outer =>
    title = "DON - Configurator"
    scene = new Scene {
      root = new BorderPane {
        padding = Insets(25)
        bottom = new Button {
          text = s"Click me to close the dialog ${Platform.currentTime}"
          onAction = handle {
            outer.close()
          }
        }
      }
    }
  }

  def receive = {
    case Protocol.Show =>
      log.info("recive: Show")
      stage.show()
    case Protocol.Close =>
      log.info("recive: Close")
      stage.close()
    case _ => ()
  }
}
