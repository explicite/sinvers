package db.service

import db.repository.{ FullInversInversRepository, FullInversRepository, HSArgumentRepository }
import db._
import reo.HSArgs
import ui.view.{ InversView, FullInversView }

object FullInversService {

  def save(inversIds: Seq[InversId], hsArgument: HSArgs): FullInversId = {
    val hsArgumentId = HSArgumentRepository.save(HSArgument(None, hsArgument.a1, hsArgument.m1, hsArgument.m2, hsArgument.m3, hsArgument.m4, hsArgument.m5, hsArgument.m6, hsArgument.m7, hsArgument.m8, hsArgument.m9, hsArgument.epsSs))
    val fullInversId = FullInversRepository.save(FullInvers(None, hsArgumentId))
    inversIds.foreach { id => FullInversInversRepository.save(FullInversInvers(None, fullInversId, id)) }
    fullInversId
  }

  def findById(id: FullInversId): FullInversView = {
    val row = FullInversRepository.findById(id)
    val hsArgRow = HSArgumentRepository.findById(row.hSArgumentId)
    val inversViews = FullInversInversRepository.findByFullInversId(row.id.get).map(_.inversId).map(InversService.findById)
    fromRow(row, inversViews, hsArgRow)
  }

  def findAll(): Seq[FullInversView] = {
    FullInversRepository.findAll().map {
      row =>
        val hsArgRow = HSArgumentRepository.findById(row.hSArgumentId)
        val inversViews = FullInversInversRepository.findByFullInversId(row.id.get).map(_.inversId).map(InversService.findById)
        fromRow(row, inversViews, hsArgRow)
    }
  }

  def deleteById(id: FullInversId): Int = {
    FullInversInversRepository.deleteByFullInversId(id)
    HSArgumentRepository.deleteById(FullInversRepository.findById(id).hSArgumentId)
    FullInversRepository.deleteById(id)
  }

  def fromRow(fullInvers: FullInvers, inversViews: Seq[InversView], hsArgument: HSArgument): FullInversView = {
    FullInversView(fullInvers.id.get,
      inversViews,
      hsArgument.a1, hsArgument.m1, hsArgument.m2, hsArgument.m3, hsArgument.m4, hsArgument.m5, hsArgument.m6, hsArgument.m7, hsArgument.m8, hsArgument.m9, hsArgument.epsSs
    )
  }

}
