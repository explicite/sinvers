package io.forge

import java.nio.file.{ Files, Path }

import io.forge.Protocol.Parameters
import regex.Parser
import util.Util

import scala.sys.process.{ ProcessLogger, Process, ProcessBuilder }

trait Environment extends Parser {

  val DON = "sym.don"
  val MESH = "work.may"
  val OUT = "file.out"
  val STEERING = "steering.dat"

  val silence = ProcessLogger((o: String) => (), (e: String) => ())

  def createEnvironment(forge: Path, parameters: Parameters): Path = {
    val environment = Files.createTempDirectory("sinvers")
    createDon(environment.resolve(DON), parameters)
    Util.copy(parameters.steering, environment.resolve(STEERING))
    Util.copy(parameters.mesh, environment.resolve(MESH))
    Util.copy(parameters.out, environment.resolve(OUT))

    environment
  }

  def processBuilder(forge: Path, environment: Path): ProcessBuilder = {
    Process(
      Seq(s"$forge", DON),
      environment.toFile,
      "PP2D_DIR" -> forge.getParent.getParent.toString,
      "FORGE2_IO" -> "BIG_ENDIAN",
      "lang" -> "eng",
      "WORK_DIR" -> environment.toString
    )
  }

  protected def clean(source: Path): Path = {
    Thread.sleep(200)
    Util.delete(source)
  }

  private def createDon(target: Path, args: Parameters): Path = {
    val don =
      s""".FICHIER\nFOUT = $OUT
          |FMAY = $MESH
          |Delete
          |.FIN FICHIER
          |.UNITES
          |mm-mpa-mm.kg.s
          |.FIN UNITES
          |.INCREMENT
          |Calage
          |.FIN INCREMENT
          |.RHEOLOGIE
          |${args.hs}
          |Coeff Poisson = 3.000000e-001
          |Module Young = 2.000000e+008
          |Temp Init = ${Util.scienceFormatter(args.temperature)}
          |Gravity
          |Inertie
          |Outil 0: Coulomb,
          |mu = 0.120000
          |.FIN RHEOLOGIE
          |.THERMIQUE
          |MVolumique = 7.800000e-006
          |Cmassique = 7.000000e+008
          |Conductmat = 2.300000e+004
          |Outil 0
          |alphat = 2.000000e+003
          |tempout = 1000.000000
          |effusoutil = 1.176362e+004
          |Face libre
          |alphat = 1.000000e+001
          |tempext = 1000.000000
          |epsilon = 8.800000e-001
          |.FIN THERMIQUE
          |.PILOTAGE
          |File = $STEERING,
          |hauteur actuelle = 12.00,
          |hauteur finale = 7.522
          |.FIN PILOTAGE
          |.EXECUTION
          |Sans Visualisation
          |.FIN EXECUTION""".stripMargin
    Util.write(target, don.getBytes)
  }

}
