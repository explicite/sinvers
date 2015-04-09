package db

import scala.slick.driver.HsqldbDriver.simple._

object DatabaseConnection {
  val database = Database.forURL("jdbc:hsqldb:mem:db", driver = "org.hsqldb.jdbcDriver", user = "sa", password = "") //Database intialization
}

trait DatabaseConnection {
  def withSession[T](f: Session => T): T = DatabaseConnection.database.withSession(f)
}