package dao

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

trait FirstNameComponent { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import driver.api._

  protected class FirstNameTable(tag: Tag) extends Table[String](tag, "first_name") {
    def name = column[String]("name")

    def * = name
  }
}

class FirstNameDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with FirstNameComponent
