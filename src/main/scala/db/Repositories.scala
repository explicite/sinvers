package db

import scala.slick.driver.HsqldbDriver.simple._
import scala.slick.jdbc.meta.MTable
import scala.util.Try

object Repositories extends DbConnection {

  //TODO create initialization
  def createSchema() = withSession {
    implicit session =>
      Try(createIfNotExists(Configurations.query, HSArguments.query, Inverses.query, FullInverses.query, FullInversInverses.query))
  }

  private def createIfNotExists(tables: TableQuery[_ <: Table[_]]*)(implicit session: Session) {
    tables foreach { table => if (MTable.getTables(table.baseTableRow.tableName).list.isEmpty) table.ddl.create }
  }

}
