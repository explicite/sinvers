package db
import scala.slick.driver.HsqldbDriver.simple._

case class FullInversId(value: Long) extends MappedTo[Long]

case class FullInvers(id: Option[FullInversId],
  hSArgumentId: HSArgumentId)

class FullInverses(tag: Tag)
    extends Table[FullInvers](tag, "full_invers") {

  //columns
  def id = column[FullInversId]("id", O.PrimaryKey, O.AutoInc)

  def hsArgumentId = column[HSArgumentId]("hs_arguments_id")

  //constraints
  def hsArgument = foreignKey("hs_arguments_fk", hsArgumentId, HSArguments.query)(_.id)

  //others
  def * = (id.?, hsArgumentId) <> (FullInvers.tupled, FullInvers.unapply)

}

object FullInverses {
  val query = TableQuery[FullInverses]
}