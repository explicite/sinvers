package io.forge

import java.nio.file.{ Files, Path }
import java.util.UUID

import util.Util

trait Environment {
  val DON = "sym.don"
  val MESH = "work.may"
  val OUT = "file.out"
  val PILOTAGE = "pilotage.dat"

  def prepare(source: Path): Path = {
    val uuid = UUID.randomUUID().toString
    val environment = source.resolve(uuid)
    Files.createDirectory(environment)
    //coping needed files
    Util.copy(source.resolve(DON), environment.resolve(DON))
    Util.copy(source.resolve(MESH), environment.resolve(MESH))
    Util.copy(source.resolve(OUT), environment.resolve(OUT))
    Util.copy(source.resolve(PILOTAGE), environment.resolve(PILOTAGE))
    environment
  }
}
