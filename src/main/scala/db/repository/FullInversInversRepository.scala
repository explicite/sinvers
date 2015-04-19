package db.repository

import db._
import scala.slick.driver.HsqldbDriver.simple._

object FullInversInversRepository extends DbConnection {

  def byIdQuery(id: FullInversInversId) = FullInversInverses.query.filter(_.id === id)

  def byFullInversIdQuery(id: FullInversId) = FullInversInverses.query.filter(_.fullInversId === id)

  def findById(id: FullInversInversId): FullInversInvers = withSession {
    implicit session =>
      byIdQuery(id).first
  }

  def findByFullInversId(id: FullInversId): Seq[FullInversInvers] = withSession {
    implicit session =>
      byFullInversIdQuery(id).list
  }

  def save(row: FullInversInvers): FullInversInversId = withSession {
    implicit session =>
      row.id match {
        case Some(id) =>
          byIdQuery(id).update(row)
          id

        case None => (FullInversInverses.query returning FullInversInverses.query.map(_.id)) += row
      }
  }

  def deleteById(id: FullInversInversId): Int = withSession {
    implicit session =>
      byIdQuery(id).delete
  }

  def deleteByFullInversId(id: FullInversId): Int = withSession {
    implicit session =>
      byFullInversIdQuery(id).delete
  }
}
