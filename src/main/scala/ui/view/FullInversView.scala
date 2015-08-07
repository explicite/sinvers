package ui.view

import db.FullInversId
import util.Util

case class FullInversView(id: FullInversId,
    inverses: Seq[InversView],
    score: Double,
    a1: Double,
    m1: Double,
    m2: Double,
    m3: Double,
    m4: Double,
    m5: Double,
    m6: Double,
    m7: Double,
    m8: Double,
    m9: Double,
    epsSs: Double) {

  val txt =
    s"""
       |Full invers result
       |score       = ${Util.scienceFormatter(score)}
       |========================================
       |m1    = ${Util.scienceFormatter(m1)}
       |m2    = ${Util.scienceFormatter(m2)}
       |m3    = ${Util.scienceFormatter(m3)}
       |m4    = ${Util.scienceFormatter(m4)}
       |m5    = ${Util.scienceFormatter(m5)}
       |m6    = ${Util.scienceFormatter(m6)}
       |m7    = ${Util.scienceFormatter(m7)}
       |m8    = ${Util.scienceFormatter(m8)}
       |m9    = ${Util.scienceFormatter(m9)}
       |eps   = ${Util.scienceFormatter(epsSs)}
       |
     """.stripMargin + inverses.map(_.txt).mkString("\n")
}
