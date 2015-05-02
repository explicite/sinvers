package ui.view

import db.InversId

case class InversView(id: InversId,
  temperature: Double,
  strainRate: Double,
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
  epsSs: Double)