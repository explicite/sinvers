package db

import scala.slick.driver.HsqldbDriver.simple._

object DbConnection {
  val userDir = System.getProperty("user.home")
  val database = Database.forURL(s"jdbc:hsqldb:file:$userDir/sinvers/db", driver = "org.hsqldb.jdbcDriver", user = "sa", password = "") //Database intialization
}

trait DbConnection {
  def withSession[T](f: Session => T): T = DbConnection.database.withSession(f)
}