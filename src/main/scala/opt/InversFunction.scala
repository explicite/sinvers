package opt

import java.io.File
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.{ Files, Path, Paths }

import data.DataFile
import io.{ DON, Forge }
import math._
import reo.HSArgs
import util.{ Util, KZ, XORShiftRandom, Persist }

case class InversFunction(forge: Forge, originalDon: DON, data: DataFile) {
  val random = new XORShiftRandom()
  val current = data.current

  val steering: Seq[(Double, Double)] = {
    val velocityStep = current.velocity.scan(0d)(_ + _).tail
    val jaw = current.jaw
    velocityStep.zip(jaw)
  }

  val pilotage = {
    val file = new File(s"${originalDon.workingDirectory}//pilotage.dat")
    Persist.zipped(steering, file)
  }

  val interval = {
    val max = 12 + 0.1
    val min = 7.522 - 0.1

    StaticInterval(min, max)
  }

  val interpolator: PolynomialSplineFunction = {
    val force = current.force
    val jaw = current.jaw //.map(12d + _)

    val (filteredForce, filteredJaw) = force.zip(jaw).filter { case (f, j) => j >= interval.min && j <= interval.max }.groupBy(_._2).map(_._2.head).toSeq.sortBy(_._2).unzip
    //val splineInterpolator = Interpolator.splineInterpolate(filteredJaw.toArray, KZ(filteredForce.toArray, 100, 2).toArray)
    val splineInterpolator = Interpolator.splineInterpolate(filteredJaw.toArray, filteredForce.toArray)

    /* def save(file: File): Unit = {
      def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
        val p = new java.io.PrintWriter(f)
        try op(p) finally p.close()
      }

      printToFile(file) {
        printWriter =>
          val toPrint = presentForce.zip(presentJaw).reverse.map {
            case (f, j) => s"0.0 $f $j 0.0 0.0 0.0 0.0 0.0 0.0"
          }
          toPrint.foreach(printWriter.println)
      }
    }

    save(new File("data.txt"))*/

    splineInterpolator
  }

  //return fitness for current context
  def fitness(args: Seq[Double]): Double = {
    val don = prepareFiles(java.util.UUID.randomUUID().toString)

    val hsArgs = HSArgs(args)
    don.updateHS(hsArgs)

    val computed = forge process don
    val fit = computed.fit(interpolator, interval)

    println(s"fitness:$fit")

    fit
  }

  private def prepareFiles(hash: String): DON = {
    def fileCopy(path: Path)(directory: (String) => String): Unit = {
      Util.retry(5) {
        Files.copy(path, Paths.get(directory(path.getFileName.toString)), REPLACE_EXISTING)
      }
    }

    def newDirectory(name: String) = s"${originalDon.workingDirectory}//$hash//$name"

    val directory = new java.io.File(newDirectory("/"))
    directory.mkdirs()

    fileCopy(originalDon.file.toPath)(newDirectory)

    //copy *.don
    val don = DON(new java.io.File(newDirectory(originalDon.name)))

    //copy *.may
    fileCopy(Paths.get(s"${originalDon.workingDirectory}//work.may"))(newDirectory)

    //copy *.out
    fileCopy(Paths.get(s"${originalDon.workingDirectory}//file.out"))(newDirectory)

    //copy *.dat
    fileCopy(Paths.get(s"${originalDon.workingDirectory}//pilotage.dat"))(newDirectory)

    don
  }

}
