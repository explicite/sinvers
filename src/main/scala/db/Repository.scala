package db

import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.meta.MTable

object Repository extends DatabaseConnection {

  class HSArguments(tag: Tag)
      extends Table[(Double, Double, Double, Double, Double, Double, Double, Double, Double, Double)](tag, "hs_arguments") {

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
    def * = (m1, m2, m3, m4, m5, m6, m7, m8, m9, epsSs)
  }

  val hsArguments = TableQuery[HSArguments]

  class Optimizations(tag: Tag)
      extends Table[(Long, Double)](tag, "optimizations") {

    //columns
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def hsArgumentId = column[Long]("hs_arguments_id")

    def temperature = column[Double]("temperature")

    //constraints
    def hsArgument = foreignKey("hs_arguments_fk", hsArgumentId, hsArguments)(_.id)

    //others
    def * = (hsArgumentId, temperature)
  }

  val optimizations = TableQuery[Optimizations]

  //TODO create initialization
  def createSchema() = withSession {
    implicit session =>
      if (MTable.getTables("optimizations").list.isEmpty) {
        (optimizations.ddl ++ hsArguments.ddl).create
      }
  }

}
