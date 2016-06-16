package dao

import javax.inject.Inject

import models.diff.Diff
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

class DiffDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile]
{
  import driver.api._

  private val Diffs = TableQuery[DiffTable]

  def all(): Future[Seq[Diff]] = db.run(Diffs.result)

  def findById(id: String): Future[Option[Diff]] = db.run(Diffs.filter(_.id === id).result.headOption)

  def latest(amount: Int): Future[Seq[Diff]] = db.run(Diffs.sortBy(_.createdAt.desc).take(amount).result)

  def insert(d: Diff): Future[Unit] = db.run(Diffs += d).map(_ => ())

  private class DiffTable(tag: Tag) extends Table[Diff](tag, "diff") {
    def id = column[String]("id", O.PrimaryKey)
    def diff = column[String]("diff")
    def createdAt = column[String]("created_at")

    def * = (id, diff, createdAt) <> (Diff.tupled, Diff.unapply)
  }
}
