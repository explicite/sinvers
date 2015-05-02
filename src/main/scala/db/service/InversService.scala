package db.service

import db.repository.{ HSArgumentRepository, InversRepository }
import db.{ HSArgument, Invers, InversId }
import ui.view.InversView

object InversService {

  def findById(id: InversId): InversView = {
    val row = InversRepository.findById(id)
    val hsArgRow = HSArgumentRepository.findById(row.argsId)
    fromRow(row, hsArgRow)
  }

  def findAll(): Seq[InversView] = {
    InversRepository.findAll().map {
      row =>
        val hsArgRow = HSArgumentRepository.findById(row.argsId)
        fromRow(row, hsArgRow)
    }
  }

  def fromRow(invers: Invers, hsArgs: HSArgument): InversView = {
    InversView(invers.id.get, invers.temperature, invers.strainRate, invers.score, hsArgs.a1, hsArgs.m1, hsArgs.m2, hsArgs.m3, hsArgs.m4, hsArgs.m5, hsArgs.m6, hsArgs.m7, hsArgs.m8, hsArgs.m9, hsArgs.epsSs)
  }

  def deleteById(id: InversId): Int = {
    val row = InversRepository.findById(id)
    InversRepository.deleteById(id)
    HSArgumentRepository.deleteById(row.argsId)
  }

}
