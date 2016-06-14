package dao

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import slick.driver.JdbcProfile

class DiffDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  private val Diffs = TableQuery[DiffTable]

  def findById(id: String): Future[Option[(String, String, String)]] =
    db.run(Diffs.filter(_.id === id).result.headOption)

  def all(): Future[Seq[(String, String, String)]] = db.run(Diffs.result)

  def insert(id: String, diff: String, createdAt: String): Future[Unit] = {
    db.run(Diffs += (id, diff, createdAt)).map(_ => ())
  }

  private class DiffTable(tag: Tag) extends Table[(String, String, String)](tag, "diff") {
    def id = column[String]("id", O.PrimaryKey)
    def diff = column[String]("diff")
    def createdAt = column[String]("created_at")

    def * = (id, diff, createdAt)
  }
}
