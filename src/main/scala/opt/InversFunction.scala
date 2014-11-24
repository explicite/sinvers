package opt

import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.{Files, Path, Paths}
import javax.swing.JFrame

import data.DataFile
import io.{DON, Forge}
import math._
import reo.HSArgs
import util.{KZ, XORShiftRandom}

import scalax.chart.module.Charting

case class InversFunction(forge: Forge, originalDon: DON, data: DataFile) extends Charting {
  val random = new XORShiftRandom()

  val forceSeries = Seq[(Double, Double)]() toXYSeriesCollection "force"
  forceSeries.removeAllSeries()
  val chart = XYLineChart(forceSeries)

  chart.toFrame().peer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  chart.show()
  val interval = {
    val current = data.current
    val max = 12 + 0.1
    val min = 7.522 - 0.1

    StaticInterval(min, max)
  }

  val interpolator: PolynomialSplineFunction = {
    val current = data.current
    val force = current.force
    val jaw = current.jaw //.map(12d + _)

    val (filteredForce, filteredJaw) = force.zip(jaw).filter { case (f, j) => j >= interval.min && j <= interval.max}.groupBy(_._2).map(_._2.head).toSeq.sortBy(_._2).unzip
    forceSeries.addSeries(filteredJaw.zip(filteredForce).toXYSeries("experiment"))
    val splineInterpolator = Interpolator.splineInterpolate(filteredJaw.toArray, filteredForce.toArray)

    /*val presentJaw = Seq.tabulate(100)(index => (((index + 1d) / 100d) * (12 - 7.522)) + 7.522)
    val presentForce = presentJaw.map(splineInterpolator(_))
    forceSeries.addSeries(presentJaw.zip(presentForce).toXYSeries("interpolated"))*/
    splineInterpolator
  }


  //return fitness for current context
  def fitness(args: Seq[Double]): Double = {
    val don = prepareFiles(random.randomAlpha(40))

    val hsArgs = HSArgs(args(0), args(1), args(2), args(3), args(4))
    //val hsArgs = HSArgs(args)
    don.updateHS(hsArgs)

    /*    val customArgs = CustomArgs(args)
        don.updateCustom(customArgs)*/

    val computed = forge process don
    /*  val computedFile = new java.io.File("C:\\Users\\Jan\\Desktop\\sym\\computed.txt")
      computed.save(computedFile)*/

    val fit = {
      val computedForce = computed.force //.map(_ * 1016.0469053138122)

      val computedFitness = computed.fit(interpolator, interval)
      //if (computedFitness < 0.001)
        forceSeries.addSeries(computed.jaw.zip(computedForce).toXYSeries(s"cf:$computedFitness"))

      computedFitness
    }

    println(s"fitness:$fit")

    fit
  }


  private def prepareFiles(hash: String): DON = {
    def fileCopy(path: Path)(directory: (String) => String): Unit = {
      try {
        Files.copy(path, Paths.get(directory(path.getFileName.toString)), REPLACE_EXISTING)
      } catch {
        case t: Throwable => fileCopy(path)(directory)
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
