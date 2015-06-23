package reo

import util.Util.scienceFormatter

/**
 *
 * @param a1 area
 * @param m1 the temperature coefficient of the material of the impact
 * @param m2 sensitivity of the deformation of the material
 * @param m3 the sensitivity of the rate of deformation of the material
 * @param m4 sensitivity of the deformation of the material
 * @param m5 temperature dependence of the deformation
 * @param m6 ???
 * @param m7 sensitivity of the deformation of the material
 * @param m8 the temperature dependence of strain rate
 * @param m9 the temperature coefficient of the material of the impact
 * @param epsSs behavior after undergoing plastic state [0,1]
 */
case class HSArgs(a1: Double = 0,
    m1: Double = 0,
    m2: Double = 0,
    m3: Double = 0,
    m4: Double = 0,
    m5: Double = 0,
    m6: Double = 0,
    m7: Double = 0,
    m8: Double = 0,
    m9: Double = 0,
    epsSs: Double = 0) {

  override def toString: String = {
    s"Thermoecroui : hanselspittelnb1,\na1=${scienceFormatter(a1)},\nm1=${scienceFormatter(m1)},\nm2=${scienceFormatter(m2)},\nm3=${scienceFormatter(m3)},\nm4=${scienceFormatter(m4)},\nm5=${scienceFormatter(m5)},\nm6=${scienceFormatter(m6)},\nm7=${scienceFormatter(m7)},\nm8=${scienceFormatter(m8)},\nm9=${scienceFormatter(m9)},\neps_ss=${scienceFormatter(epsSs)}\n"
  }
}

object HSArgs {
  def apply(args: Seq[Double]): HSArgs = {
    args match {
      case a1 :: m1 :: m2 :: m3 :: m4 :: Nil => HSArgs(a1, m1, m2, m3, m4, 0d, 0d, 0d, 0d, 0d, 0d)
      case sx: Seq[Double] if sx.size == 11  => HSArgs(args.head, args(1), args(2), args(3), args(4), args(5), args(6), args(7), args(8), args(9), args(10))
      case _                                 => throw new Exception("not satisfied numbers of arguments")
    }
  }

  def apply(a1: Double,
    m1: Double,
    m2: Double,
    m3: Double,
    m4: Double): HSArgs = HSArgs(a1, m1, m2, m3, m4, 0d, 0d, 0d, 0d, 0d, 0d)

  def apply(a1: Double,
    m1: Double,
    m2: Double,
    m3: Double,
    m4: Double,
    m5: Double,
    m6: Double,
    m7: Double,
    m8: Double,
    m9: Double): HSArgs = HSArgs(a1, m1, m2, m3, m4, m5, m6, m7, m8, m9, 0d)

}

