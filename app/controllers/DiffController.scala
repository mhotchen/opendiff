package controllers

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

import dao.{CommentDao, DiffDao}
import models.diff.form.DiffForm
import models.diff.Diff
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.io.Source

class DiffController @Inject()(val messagesApi: MessagesApi, val diffDao: DiffDao, val commentDao: CommentDao)
  extends Controller with I18nSupport
{
  def get(requestedId: String) = Action.async {
    val comments = commentDao.findByDiffId(requestedId)
    diffDao.findById(requestedId).map {
      case Some(diff) => Ok(views.html.diff(diff, Await.result(comments, Duration(2, "s"))))
      case None =>
        val testDiff = Source.fromFile("diff-tests/git/diff").mkString
        Ok(views.html.diff(Diff(Some(requestedId), testDiff, "2016-06-06T00:00:00.000+000"), Await.result(comments, Duration(2, "s"))))
    }
  }

  def create = Action.async { implicit request =>
    DiffForm.form.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.welcome(formWithErrors, Nil)))
      },
      userData => {
        diffDao
          .insert(Diff(None, userData.diff, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
          .map(d => Redirect(routes.DiffController.get(d.id.get)))
      }
    )
  }
}
