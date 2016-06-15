package controllers

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

import dao.DiffDao
import models.diff.form.DiffForm
import models.diff.{Diff, DiffError}
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

class DiffController @Inject()(val messagesApi: MessagesApi, val diffDao: DiffDao)
  extends Controller with I18nSupport
{

  def get(requestedId: String) = Action.async {
    diffDao.findById(requestedId).map {
      case Some(diff) =>
        val parsedDiff = Diff(diff._2)
        parsedDiff match {
          case d: Diff => Ok(views.html.diff(d, diff._2))
          case DiffError(_) => InternalServerError
        }
      case None =>
        val testDiff = Source.fromFile("diff-tests/git/diff").mkString
        val parsedDiff = Diff(testDiff)
        parsedDiff match {
          case d: Diff => Ok(views.html.diff(d, testDiff))
          case DiffError(e) => println(e); InternalServerError
        }
    }
  }

  def create = Action.async { implicit request =>
    DiffForm.form.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.welcome(formWithErrors)))
      },
      userData => {
        val id = java.util.UUID.randomUUID.toString
        val createdAt = LocalDateTime.now()
        diffDao
          .insert(id, userData.diff, createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
          .map(_ => Redirect(routes.DiffController.get(id.toString)))
      }
    )
  }
}
