package regex

trait Parser {
  protected val ForgingLoadRegex = """(-?\d*.?)(\d+)(\s*Tonnes\s*on\s*die\s*2)""".r
  protected val VirtualLoadRegex = """(virtual forging load\s*:\s*)(-?\+?\d+.)(\d*E?\+?\-*\d+)?""".r
  protected val HeightRegex = """(height\s*:\s*)(-?\+?\d+\.*)(\d*E?\+?\-*\d+)?""".r

  //ERRORS
  protected val ReloadRegex = """The process cannot access""".r

  //Hansel-Spittel
  object DONRegex {
    val a1Regex = """(a1=)(-?\+?\d+\.*)(\d*E?\+?\-*\d+)?""".r
    val m1Regex = """(m1=)(-?\+?\d+\.*)(\d*E?\+?\-*\d+)?""".r
    val m2Regex = """(m2=)(-?\+?\d+\.*)(\d*E?\+?\-*\d+)?""".r
    val m3Regex = """(m3=)(-?\+?\d+\.*)(\d*E?\+?\-*\d+)?""".r
    val m4Regex = """(m4=)(-?\+?\d+\.*)(\d*E?\+?\-*\d+)?""".r
    val m5Regex = """(m5=)(-?\+?\d+\.*)(\d*E?\+?\-*\d+)?""".r
    val m6Regex = """(m6=)(-?\+?\d+\.*)(\d*E?\+?\-*\d+)?""".r
    val m7Regex = """(m7=)(-?\+?\d+\.*)(\d*E?\+?\-*\d+)?""".r
    val m8Regex = """(m8=)(-?\+?\d+\.*)(\d*E?\+?\-*\d+)?""".r
    val m9Regex = """(m9=)(-?\+?\d+\.*)(\d*E?\+?\-*\d+)?""".r
    val epsSsRegex = """(eps_ss=)(-?\+?\d+\.*)(\d*E?\+?\-*\d+)?""".r
  }

  protected def formatDouble(mantissa: String, exponent: String): Double = {
    def validDouble(double: String) = {
      double match {
        case null => "0"
        case _ => double
      }
    }

    val validMantissa = validDouble(mantissa)
    val validExponent = validDouble(exponent)

    (validMantissa + validExponent).toDouble
  }
}
