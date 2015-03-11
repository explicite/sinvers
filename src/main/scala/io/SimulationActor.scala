package io

import java.nio.file.Path

import akka.actor.Actor
import io.forge.Environment

import scala.sys.process._

class SimulationActor extends Actor with Environment {
  var environment: Path = null

  def receive = {
    case Start(forge, source) =>
      val environment = prepare(source)
      val process =
        Process(
          Seq(s"$forge\\bin\\xf2_p1.exe", DON),
          environment.toFile,
          "PP2D_DIR" -> forge.toString,
          "FORGE2_IO" -> "BIG_ENDIAN",
          "lang" -> "eng",
          "WORK_DIR" -> "don.workingDirectory"
        )

  }

}

