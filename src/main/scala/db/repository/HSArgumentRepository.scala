package db.repository

import db.{ HSArguments, HSArgument, HSArgumentId, DbConnection }
import scala.slick.driver.HsqldbDriver.simple._

object HSArgumentRepository extends DbConnection {
  def byIdQuery(id: HSArgumentId) = HSArguments.query.filter(_.id === id)

  def findById(id: HSArgumentId): HSArgument = withSession {
    implicit session =>
      byIdQuery(id).first
  }

  def save(row: HSArgument): HSArgumentId = withSession {
    implicit session =>
      row.id match {
        case Some(id) =>
          byIdQuery(id).update(row)
          id
        case None => (HSArguments.query returning HSArguments.query.map(_.id)) += row
      }
  }

  def deleteById(id: HSArgumentId): Int = withSession {
    implicit session =>
      byIdQuery(id).delete
  }

}
