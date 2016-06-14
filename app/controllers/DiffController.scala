package controllers

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

import dao.DiffDao
import models.diff.form.DiffForm
import models.diff.parsers.UnifiedDiffParser
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DiffController @Inject()(val messagesApi: MessagesApi, val diffDao: DiffDao)
  extends Controller with I18nSupport
{

  def get(requestedId: String) = Action.async {
    diffDao.findById(requestedId).map {
      case Some(diff) => Ok(views.html.diff(UnifiedDiffParser.parse(diff._2), diff._2))
      case None => NotFound
    }
  }

  def create = Action.async { implicit request =>
    DiffForm.form.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.welcome(formWithErrors)))
      },
      userData => {
        val diff = if (userData.diff.endsWith("\n")) userData.diff else userData.diff + "\n"
        val id = java.util.UUID.randomUUID.toString
        val createdAt = LocalDateTime.now()
        diffDao
          .insert(id, diff, createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
          .map(_ => Redirect(routes.DiffController.get(id.toString)))
      }
    )
  }
}
