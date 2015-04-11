package db.service

import db.repository.{ HSArgumentRepository, SimulationRepository }
import db.{ HSArgument, Simulation, SimulationId }
import ui.view.SimulationView

object SimulationService {

  def findById(id: SimulationId): SimulationView = {
    val simulationRow = SimulationRepository.findById(id)
    val hsArgRow = HSArgumentRepository.findById(simulationRow.argsId)
    fromRow(simulationRow, hsArgRow)
  }

  def findAll(): Seq[SimulationView] = {
    SimulationRepository.findAll().map {
      simulationRow =>
        val hsArgRow = HSArgumentRepository.findById(simulationRow.argsId)
        fromRow(simulationRow, hsArgRow)
    }
  }

  def fromRow(simulation: Simulation, hsArgs: HSArgument): SimulationView = {
    SimulationView(simulation.id.get, simulation.temperature, simulation.strainRate, hsArgs.a1, hsArgs.m1, hsArgs.m2, hsArgs.m3, hsArgs.m4, hsArgs.m5, hsArgs.m6, hsArgs.m7, hsArgs.m8, hsArgs.m9, hsArgs.epsSs)
  }

  def deleteById(id: SimulationId): Int = {
    val simulationRow = SimulationRepository.findById(id)
    SimulationRepository.deleteById(id)
    HSArgumentRepository.deleteById(simulationRow.argsId)
  }

}
