package db

import scala.slick.driver.HsqldbDriver.simple._

case class Configuration(key: String, value: String)

class Configurations(tag: Tag) extends Table[Configuration](tag, "configurations") {

  //columns
  def key = column[String]("key")
  def value = column[String]("value")

  //others
  def * = (key, value) <> (Configuration.tupled, Configuration.unapply)

}

object Configurations {
  val query = TableQuery[Configurations]

  //configuration keys
  val forge = "forge"
}
