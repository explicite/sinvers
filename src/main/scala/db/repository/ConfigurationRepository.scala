package db.repository

import db.{ DbConnection, Configurations, Configuration }
import scala.slick.driver.HsqldbDriver.simple._

object ConfigurationRepository extends DbConnection {

  def byKeyQuery(key: String) = Configurations.query.filter(_.key === key)

  def findByKey(key: String): Option[Configuration] = withSession {
    implicit session => byKeyQuery(key).firstOption
  }

  def save(row: Configuration): String = withSession {
    implicit session =>
      findByKey(row.key) match {
        case Some(r) =>
          byKeyQuery(row.key).update(row)
          row.key
        case None => (Configurations.query returning Configurations.query.map(_.key)) += row
      }
  }

  def deleteByKey(key: String): Int = withSession {
    implicit session =>
      byKeyQuery(key).delete
  }

}
