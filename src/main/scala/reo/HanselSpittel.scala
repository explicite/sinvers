package reo

import java.io.File

import io.DON
import regex.Parser

import scala.io.Source

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
 * @param epsSs
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

  def formatter(d: Double): String = new java.text.DecimalFormat("0.##############E0").format(d)

  override def toString: String = {
    s"Thermoecroui : hanselspittelnb1,\na1=${formatter(a1)},\nm1=${formatter(m1)},\nm2=${formatter(m2)},\nm3=${formatter(m3)},\nm4=${formatter(m4)},\nm5=${formatter(m5)},\nm6=${formatter(m6)},\nm7=${formatter(m7)},\nm8=${formatter(m8)},\nm9=${formatter(m9)},\neps_ss=${formatter(epsSs)}\n"
  }
}

object HSArgs {
  def apply(args: Seq[Double]): HSArgs = {
    assert(args.size == 11, "not satisfied numbers of arguments")
    HSArgs(args(0), args(1), args(2), args(3), args(4), args(5), args(6), args(7), args(8), args(9), args(10))
  }

  def apply(a1: Double,
            m1: Double,
            m2: Double,
            m3: Double,
            m4: Double): HSArgs = {
    HSArgs(a1, m1, m2, m3, m4, 0d, 0d, 0d, 0d, 0d, 0d)
  }
}

case class HanselSpittel(file: File) extends Parser {
  val don = DON(file)

  def update(hsArgs: HSArgs) = {
    don.updateHS(hsArgs)
  }

  def current: HSArgs = {
    import DONRegex._
    val source = Source fromFile file
    val lines = source.getLines()
    val a1 = (for {
      line <- lines
      a1Regex(_, a1Mantissa, a1Exponent) <- a1Regex.findFirstIn(line)
    } yield formatDouble(a1Mantissa, a1Exponent)).toSeq.headOption.getOrElse(throw new Exception("cannot find a1"))
    val m1 = (for {
      line <- lines
      m1Regex(_, m1Mantissa, m1Exponent) <- m1Regex.findFirstIn(line)
    } yield formatDouble(m1Mantissa, m1Exponent)).toSeq.headOption.getOrElse(throw new Exception("cannot find m1"))
    val m2 = (for {
      line <- lines
      m2Regex(_, m2Mantissa, m2Exponent) <- m2Regex.findFirstIn(line)
    } yield formatDouble(m2Mantissa, m2Exponent)).toSeq.headOption.getOrElse(throw new Exception("cannot find m2"))
    val m3 = (for {
      line <- lines
      m3Regex(_, m3Mantissa, m3Exponent) <- m3Regex.findFirstIn(line)
    } yield formatDouble(m3Mantissa, m3Exponent)).toSeq.headOption.getOrElse(throw new Exception("cannot find m3"))
    val m4 = (for {
      line <- lines
      m4Regex(_, m4Mantissa, m4Exponent) <- m4Regex.findFirstIn(line)
    } yield formatDouble(m4Mantissa, m4Exponent)).toSeq.headOption.getOrElse(throw new Exception("cannot find m4"))
    val m5 = (for {
      line <- lines
      m5Regex(_, m5Mantissa, m5Exponent) <- m5Regex.findFirstIn(line)
    } yield formatDouble(m5Mantissa, m5Exponent)).toSeq.headOption.getOrElse(throw new Exception("cannot find m5"))
    val m6 = (for {
      line <- lines
      m6Regex(_, m6Mantissa, m6Exponent) <- m6Regex.findFirstIn(line)
    } yield formatDouble(m6Mantissa, m6Exponent)).toSeq.headOption.getOrElse(throw new Exception("cannot find m6"))
    val m7 = (for {
      line <- lines
      m7Regex(_, m7Mantissa, m7Exponent) <- m7Regex.findFirstIn(line)
    } yield formatDouble(m7Mantissa, m7Exponent)).toSeq.headOption.getOrElse(throw new Exception("cannot find m7"))
    val m8 = (for {
      line <- lines
      m8Regex(_, m8Mantissa, m8Exponent) <- m8Regex.findFirstIn(line)
    } yield formatDouble(m8Mantissa, m8Exponent)).toSeq.headOption.getOrElse(throw new Exception("cannot find m8"))
    val m9 = (for {
      line <- lines
      m9Regex(_, m9Mantissa, m9Exponent) <- m9Regex.findFirstIn(line)
    } yield formatDouble(m9Mantissa, m9Exponent)).toSeq.headOption.getOrElse(throw new Exception("cannot find m9"))
    val epsSs = (for {
      line <- lines
      epsSsRegex(_, epsSsMantissa, epsSsExponent) <- epsSsRegex.findFirstIn(line)
    } yield formatDouble(epsSsMantissa, epsSsExponent)).toSeq.headOption.getOrElse(throw new Exception("cannot find epsSs"))

    source.close()
    HSArgs(a1, m1, m2, m3, m4, m5, m6, m7, m8, m9, epsSs)
  }
}

