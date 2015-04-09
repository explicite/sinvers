package db

import scala.slick.driver.HsqldbDriver.simple._
import scala.slick.jdbc.meta.MTable

object Repository extends DatabaseConnection {

  case class HSArgument(id: Option[Long],
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
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

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
    def * = (id.?, m1, m2, m3, m4, m5, m6, m7, m8, m9, epsSs) <> (HSArgument.tupled, HSArgument.unapply)
  }

  val hsArguments = TableQuery[HSArguments]

  case class Optimization(id: Option[Long],
    argsId: Long,
    temperature: Double)

  class Optimizations(tag: Tag)
      extends Table[Optimization](tag, "optimizations") {

    //columns
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def hsArgumentId = column[Long]("hs_arguments_id")

    def temperature = column[Double]("temperature")

    //constraints
    def hsArgument = foreignKey("hs_arguments_fk", hsArgumentId, hsArguments)(_.id)

    //others
    def * = (id.?, hsArgumentId, temperature) <> (Optimization.tupled, Optimization.unapply)
  }

  val optimizations = TableQuery[Optimizations]

  //TODO create initialization
  def createSchema() = withSession {
    implicit session =>
      createIfNotExists(hsArguments, optimizations)
  }

  private def createIfNotExists(tables: TableQuery[_ <: Table[_]]*)(implicit session: Session) {
    tables foreach { table => if (MTable.getTables(table.baseTableRow.tableName).list.isEmpty) table.ddl.create }
  }

}
