package dao

import javax.inject.Inject

import models.diff.Diff
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

trait DiffComponent { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import driver.api._

  protected class DiffTable(tag: Tag) extends Table[Diff](tag, "diff") {
    def id = column[String]("id", O.PrimaryKey)
    def diff = column[String]("diff")
    def createdAt = column[String]("created_at")

    def * = (id.?, diff, createdAt) <> (Diff.tupled, Diff.unapply)
  }
}

class DiffDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with DiffComponent with AnimalComponent with FirstNameComponent
{
  import driver.api._

  private val Animals = TableQuery[AnimalTable]
  private val FirstNames = TableQuery[FirstNameTable]
  private val Diffs = TableQuery[DiffTable]
  private val rand = SimpleFunction.nullary[Double]("rand")
  private val concat3 = SimpleFunction.ternary[String, String, String, String]("concat")

  def findById(id: String): Future[Option[Diff]] = db.run(Diffs.filter(_.id === id).result.headOption)

  def latest(amount: Int): Future[Seq[Diff]] = db.run(Diffs.sortBy(_.createdAt.desc).take(amount).result)

  def insert(d: Diff): Future[Diff] = d.id match {
    case Some(_) => db.run(Diffs += d).map(_ => d)
    case None =>
      val diff = Diff(generateId, d.diff, d.createdAt)
      db.run(Diffs += diff).map(_ => diff)
  }

  private def generateId = Await.result(randomAnimalWithName, Duration(20, "s")) match {
    case Some(id) => Some(id)
    case None => throw new RuntimeException("Timeout generating ID")
  }

  private def randomAnimalWithName: Future[Option[String]] = db.run(
    (for {
      a <- Animals.sortBy(x => rand)
      n <- FirstNames.sortBy(x => rand)
      if Diffs.filter(concat3(n.name, "The", a.name) === _.id).length === 0
    } yield concat3(n.name, "The", a.name))
      .take(1)
      .result
      .headOption
  )
}
