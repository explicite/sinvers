package db.repository

import db.{ Invers, Inverses, InversId, DbConnection }
import scala.slick.driver.HsqldbDriver.simple._

object InversRepository extends DbConnection {

  def byIdQuery(id: InversId) = Inverses.query.filter(_.id === id)

  def findById(id: InversId): Invers = withSession {
    implicit session =>
      byIdQuery(id).first
  }

  def findAll(): Seq[Invers] = withSession {
    implicit session =>
      Inverses.query.list
  }

  def save(row: Invers): InversId = withSession {
    implicit session =>
      row.id match {
        case Some(id) =>
          byIdQuery(id).update(row)
          id
        case None => (Inverses.query returning Inverses.query.map(_.id)) += row
      }
  }

  def deleteById(id: InversId): Int = withSession {
    implicit session =>
      byIdQuery(id).delete
  }

}
