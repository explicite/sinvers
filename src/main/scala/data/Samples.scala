package data

import java.nio.file.{ Path, Paths }

case class Samples(path: Path, max: Double) {
  val mesh = path.resolve("mesh.may")
  val tooling = path.resolve("tooling.out")

  override def toString: String = path.getFileName.toString
}

object Samples {
  private def path(sample: String): Path = {
    val url = {
      val full = getClass.getResource("/forge/10x12").getPath
      if (full.startsWith("/")) full.drop(1) else full
    }
    Paths.get(url)
  }
  val TenToTwelve = Samples(path("10x12"), 12)
}

