package data

sealed trait Force {
  val conversion: Double

  def fromTones(value: Double): Double = value * conversion

  def toTones(value: Double): Double = value / conversion
}

object KGF extends Force {
  val conversion = 1016.0469053138122
}

object Tones extends Force {
  val conversion = 1.0
}
