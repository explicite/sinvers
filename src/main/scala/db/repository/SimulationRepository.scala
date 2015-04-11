package db.repository

import db.{ Simulation, Simulations, SimulationId, DbConnection }
import scala.slick.driver.HsqldbDriver.simple._

object SimulationRepository extends DbConnection {

  def byIdQuery(id: SimulationId) = Simulations.query.filter(_.id === id)

  def findById(id: SimulationId): Simulation = withSession {
    implicit session =>
      byIdQuery(id).first
  }

  def findAll(): Seq[Simulation] = withSession {
    implicit session =>
      Simulations.query.list
  }

  def save(row: Simulation): SimulationId = withSession {
    implicit session =>
      row.id match {
        case Some(id) =>
          byIdQuery(id).update(row)
          id
        case None => (Simulations.query returning Simulations.query.map(_.id)) += row
      }
  }

  def deleteById(id: SimulationId): Int = withSession {
    implicit session =>
      byIdQuery(id).delete
  }
}
