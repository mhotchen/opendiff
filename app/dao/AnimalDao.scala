package dao

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

trait AnimalComponent { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import driver.api._

  protected class AnimalTable(tag: Tag) extends Table[String](tag, "animal") {
    def name = column[String]("name")

    def * = name
  }
}

class AnimalDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with AnimalComponent
