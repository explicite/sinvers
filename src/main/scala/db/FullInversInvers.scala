package db
import scala.slick.driver.HsqldbDriver.simple._

case class FullInversInversId(value: Long) extends MappedTo[Long]

case class FullInversInvers(id: Option[FullInversInversId],
  fullInversId: FullInversId,
  inversId: InversId)

class FullInversInverses(tag: Tag)
    extends Table[FullInversInvers](tag, "full_invers_inverses") {

  //columns
  def id = column[FullInversInversId]("id", O.PrimaryKey, O.AutoInc)

  def fullInversId = column[FullInversId]("full_invers_id")

  def inversId = column[InversId]("invers_id")

  //constraints
  def fullInvers = foreignKey("fii_full_invers_fk", fullInversId, FullInverses.query)(_.id)

  def invers = foreignKey("fii_invers_fk", inversId, Inverses.query)(_.id)

  //others
  def * = (id.?, fullInversId, inversId) <> (FullInversInvers.tupled, FullInversInvers.unapply)

}

object FullInversInverses {
  val query = TableQuery[FullInversInverses]
}
