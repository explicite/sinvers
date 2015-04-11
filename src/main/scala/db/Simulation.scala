package db
import scala.slick.driver.HsqldbDriver.simple._

case class SimulationId(value: Long) extends MappedTo[Long]

case class Simulation(id: Option[SimulationId],
  argsId: HSArgumentId,
  temperature: Double)

class Simulations(tag: Tag)
    extends Table[Simulation](tag, "simulations") {

  //columns
  def id = column[SimulationId]("id", O.PrimaryKey, O.AutoInc)

  def hsArgumentId = column[HSArgumentId]("hs_arguments_id")

  def temperature = column[Double]("temperature")

  //constraints
  def hsArgument = foreignKey("hs_arguments_fk", hsArgumentId, HSArguments.query)(_.id)

  //others
  def * = (id.?, hsArgumentId, temperature) <> (Simulation.tupled, Simulation.unapply)

}

object Simulations {
  val query = TableQuery[Simulations]
}
