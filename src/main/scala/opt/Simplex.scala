package opt

case class Simplex(points: Seq[Seq[Double]])(implicit context: EvaluationContext) {

  import context._

  val values = points map function
  val min = points(values.indexOf(values.min))
  val max = points(values.indexOf(values.max))

  def transform = Simplex {
    val cog = points.filter(_ != max).foldLeft(Seq.fill(points.head.size)(0d))((r, c) => r.zip(c) map { case (re, ce) => re + ce}) map (_ / (points.size - 1))
    val reflectionPoint = cog.zip(cog.zip(max) map { case (re, ce) => (re - ce) * α}) map { case (re, ce) => re + ce}
    if (function(min) <= function(reflectionPoint) && function(reflectionPoint) < function(max)) {
      points map { point => if (point == max) reflectionPoint else point}
    } else if (function(reflectionPoint) < function(min)) {
      val expansionPoint = cog.zip(reflectionPoint.zip(cog) map { case (re, ce) => (re - ce) * γ}) map { case (re, ce) => re + ce}
      if (function(expansionPoint) < function(reflectionPoint))
        points map { point => if (point == max) expansionPoint else point}
      else
        points map { point => if (point == max) reflectionPoint else point}
    } else {
      val narrowPoint = cog.zip(max.zip(cog) map { case (re, ce) => (re - ce) * β}) map { case (re, ce) => re + ce}
      if (function(narrowPoint) < function(max))
        points map { point => if (point == max) narrowPoint else point}
      else
        points map { point => if (point != min) point.zip(min) map { case (re, ce) => (re + ce) * δ} else point}
    }
  }

  def transformable(ε: Double): Boolean = !points.filter(_ != min).forall(point => scala.math.sqrt(point.zip(min).map(c => (c._1 - c._2) * (c._1 - c._2)).sum) < ε)

}
