package io.forge

import java.nio.file.{ Files, Path }
import java.util.UUID

import io.DONArgs
import regex.Parser
import util.Util

import scala.collection.mutable.ListBuffer
import scala.sys.process.{ Process, ProcessBuilder, ProcessIO }

trait Environment extends Parser {
  protected val time = ListBuffer[Double]()
  protected val load = ListBuffer[Double]()
  protected val height = ListBuffer[Double]()
  protected val velocity = ListBuffer[Double]()

  var environment: Path = null
  var process: Process = null
  var uuid: String = null

  val DON = "sym.don"
  val MESH = "work.may"
  val OUT = "file.out"

  def environment(forge: Path, source: Path, args: DONArgs): Path = {
    uuid = UUID.randomUUID().toString
    val environment = source.resolve(uuid)
    Files.createDirectory(environment)
    //coping needed files
    createDon(environment.resolve(DON), args)
    Util.copy(source.resolve(args.steering), environment.resolve(args.steering))
    Util.copy(source.resolve(MESH), environment.resolve(MESH))
    Util.copy(source.resolve(OUT), environment.resolve(OUT))

    environment
  }

  def processBuilder(forge: Path, environment: Path): ProcessBuilder = {
    Process(
      Seq(s"${forge.resolve("bin/xf2_p1.exe")}", DON),
      environment.toFile,
      "PP2D_DIR" -> forge.toAbsolutePath.toString,
      "FORGE2_IO" -> "BIG_ENDIAN",
      "lang" -> "eng",
      "WORK_DIR" -> environment.toString
    )
  }

  def processIO = new ProcessIO(
    in => (),
    data => try {
      scala.io.Source.fromInputStream(data).getLines().foreach(processDataLine)
    } finally {
      data.close()
    },
    error => error.close()
  )

  protected def clean(source: Path): Path = {
    if (process != null)
      process.destroy()

    Thread.sleep(200)
    Util.delete(source)
  }

  private def createDon(target: Path, args: DONArgs): Path = {
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
          |Temp Init = ${Util.formatter(args.temperature)}
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
          |File = ${args.steering},
          |hauteur actuelle = 12.00,
          |hauteur finale = 7.522
          |.FIN PILOTAGE
          |.EXECUTION
          |Sans Visualisation
          |.FIN EXECUTION""".stripMargin
    Util.write(target, don.getBytes)
  }

  private def processDataLine(line: String): Unit = {
    IncrementTimeRegex findFirstIn line match {
      case Some(IncrementTimeRegex(_, mantissa, exponent)) => time += formatDouble(mantissa, exponent)
      case None => Unit
    }
    VirtualLoadRegex findFirstIn line match {
      case Some(VirtualLoadRegex(_, mantissa, exponent)) => load += formatDouble(mantissa, exponent)
      case None => Unit
    }
    HeightRegex findFirstIn line match {
      case Some(HeightRegex(_, mantissa, exponent)) => height += formatDouble(mantissa, exponent)
      case None                                     => Unit
    }
    VelocityRegex findFirstIn line match {
      case Some(VelocityRegex(_, mantissa, exponent)) => velocity += formatDouble(mantissa, exponent)
      case None                                       => Unit
    }
  }

}
