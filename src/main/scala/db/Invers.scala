package db
import scala.slick.driver.HsqldbDriver.simple._

case class InversId(value: Long) extends MappedTo[Long]

case class Invers(id: Option[InversId],
  argsId: HSArgumentId,
  temperature: Double,
  strainRate: Double)

class Inverses(tag: Tag)
    extends Table[Invers](tag, "inverses") {

  //columns
  def id = column[InversId]("id", O.PrimaryKey, O.AutoInc)

  def hsArgumentId = column[HSArgumentId]("hs_arguments_id")

  def temperature = column[Double]("temperature")

  def strainRate = column[Double]("strain_rate")

  //constraints
  def hsArgument = foreignKey("hs_arguments_fk", hsArgumentId, HSArguments.query)(_.id)

  //others
  def * = (id.?, hsArgumentId, temperature, strainRate) <> (Invers.tupled, Invers.unapply)

}

object Inverses {
  val query = TableQuery[Inverses]
}
