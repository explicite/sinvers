package io.forge

import java.nio.file.{ Files, Path }

import io.forge.Protocol.Parameters
import regex.Parser
import util.Util

import scala.collection.mutable.ListBuffer
import scala.sys.process.{ ProcessLogger, Process, ProcessBuilder }

trait Environment extends Parser {

  protected var time = ListBuffer[Double]()
  protected var load = ListBuffer[Double]()
  protected var height = ListBuffer[Double]()
  protected var velocity = ListBuffer[Double]()

  val DON = "sym.don"
  val MESH = "work.may"
  val OUT = "file.out"
  val STEERING = "steering.dat"

  val silence = ProcessLogger((o: String) => (), (e: String) => throw new Exception(e))

  def createEnvironment(forge: Path, parameters: Parameters): Path = {
    val environment = Files.createTempDirectory("sinvers")
    createDon(environment.resolve(DON), parameters)
    Util.copy(parameters.steering, environment.resolve(STEERING))
    Util.copy(parameters.sample.mesh, environment.resolve(MESH))
    Util.copy(parameters.sample.tooling, environment.resolve(OUT))

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
          |tempout = ${Util.trimmedFormatter(args.temperature)}
          |effusoutil = 1.176362e+004
          |Face libre
          |alphat = 1.000000e+001
          |tempext = ${Util.trimmedFormatter(args.temperature)}
          |epsilon = 8.800000e-001
          |.FIN THERMIQUE
          |.PILOTAGE
          |File = $STEERING,
          |hauteur actuelle = ${Util.trimmedFormatter(args.interval.max)},
          |hauteur finale = ${Util.trimmedFormatter(args.interval.min)}
          |.FIN PILOTAGE
          |.EXECUTION
          |Sans Visualisation
          |.FIN EXECUTION""".stripMargin
    Util.write(target, don.getBytes)
  }

}
