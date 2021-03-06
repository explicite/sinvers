package db

import scala.slick.driver.HsqldbDriver.simple._

case class HSArgumentId(value: Long) extends MappedTo[Long]

case class HSArgument(id: Option[HSArgumentId],
  a1: Double,
  m1: Double,
  m2: Double,
  m3: Double,
  m4: Double,
  m5: Double,
  m6: Double,
  m7: Double,
  m8: Double,
  m9: Double,
  epsSs: Double)

class HSArguments(tag: Tag)
    extends Table[HSArgument](tag, "hs_arguments") {

  //columns
  def id = column[HSArgumentId]("id", O.PrimaryKey, O.AutoInc)

  def a1 = column[Double]("a1")

  def m1 = column[Double]("m1")

  def m2 = column[Double]("m2")

  def m3 = column[Double]("m3")

  def m4 = column[Double]("m4")

  def m5 = column[Double]("m5")

  def m6 = column[Double]("m6")

  def m7 = column[Double]("m7")

  def m8 = column[Double]("m8")

  def m9 = column[Double]("m9")

  def epsSs = column[Double]("eps_ss")

  //others
  def * = (id.?, a1, m1, m2, m3, m4, m5, m6, m7, m8, m9, epsSs) <> (HSArgument.tupled, HSArgument.unapply)

}

object HSArguments {
  val query = TableQuery[HSArguments]
}
