package db.repository

import db.{ DbConnection, FullInvers, FullInverses, FullInversId }
import scala.slick.driver.HsqldbDriver.simple._

object FullInversRepository extends DbConnection {

  def byIdQuery(id: FullInversId) = FullInverses.query.filter(_.id === id)

  def findById(id: FullInversId): FullInvers = withSession {
    implicit session =>
      byIdQuery(id).first
  }

  def findAll(): Seq[FullInvers] = withSession {
    implicit session =>
      FullInverses.query.list
  }

  def save(row: FullInvers): FullInversId = withSession {
    implicit session =>
      row.id match {
        case Some(id) =>
          byIdQuery(id).update(row)
          id
        case None => (FullInverses.query returning FullInverses.query.map(_.id)) += row
      }
  }

  def deleteById(id: FullInversId): Int = withSession {
    implicit session =>
      byIdQuery(id).delete
  }

}
