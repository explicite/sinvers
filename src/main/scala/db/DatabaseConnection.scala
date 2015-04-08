package db

import scala.slick.driver.H2Driver.simple._

object DatabaseConnection {
  val database = Database.forURL("jdbc:h2:file:~/data/db", driver = "org.h2.Driver") //Database intialization
}

trait DatabaseConnection {
  def withSession[T](f: Session => T): T = DatabaseConnection.database.withSession(f)
}