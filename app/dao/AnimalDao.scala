package dao

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.Future

class AnimalDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with FirstNameComponent with DiffComponent
{
  import driver.api._

  private val Animals = TableQuery[AnimalTable]
  private val FirstNames = TableQuery[FirstNameTable]
  private val Diffs = TableQuery[DiffTable]
  private val rand = SimpleFunction.nullary[Double]("rand")
  private val concat3 = SimpleFunction.ternary[String, String, String, String]("concat")

  def randomAnimalWithName: Future[Option[String]] = db.run(
    (for {
      a <- Animals.sortBy(x => rand)
      n <- FirstNames.sortBy(x => rand)
      if Diffs.filter(concat3(n.name, "The", a.name) === _.id).length === 0
    } yield concat3(n.name, "The", a.name))
      .take(1)
      .result
      .headOption
  )

  private class AnimalTable(tag: Tag) extends Table[String](tag, "animal") {
    def name = column[String]("name")

    def * = name
  }
}
