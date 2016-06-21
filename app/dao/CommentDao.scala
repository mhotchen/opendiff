package dao

import javax.inject.Inject

import models.comment.Comment
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CommentDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile]
{
  import driver.api._

  private val Comments = TableQuery[CommentTable]

  def insert(c: Comment): Future[Unit] = db.run(Comments += c).map(_ => ())

  def findByDiffId(dId: String): Future[Seq[Comment]] = db.run(Comments.filter(_.diffId === dId).result)

  private class CommentTable(tag: Tag) extends Table[Comment](tag, "comment") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def comment = column[String]("comment")
    def diffId = column[String]("diff_id")
    def originalFile = column[String]("original_file")
    def changedFile = column[String]("changed_file")
    def lineNumber = column[Int]("line_number")
    def onOriginal = column[Boolean]("on_original")

    def * = (id.?, comment, diffId, originalFile, changedFile, lineNumber, onOriginal) <> (Comment.tupled, Comment.unapply)
  }
}
