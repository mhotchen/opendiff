package controllers

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

import dao.{AnimalDao, DiffDao}
import models.diff.form.DiffForm
import models.diff.Diff
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.io.Source

class DiffController @Inject()(val messagesApi: MessagesApi, val diffDao: DiffDao, val animalDao: AnimalDao)
  extends Controller with I18nSupport
{

  def get(requestedId: String) = Action.async {
    diffDao.findById(requestedId).map {
      case Some(diff) => Ok(views.html.diff(diff))
      case None =>
        val testDiff = Source.fromFile("diff-tests/git/diff").mkString
        Ok(views.html.diff(Diff("0000", testDiff, "2016-06-06T00:00:00.000+000")))
    }
  }

  def create = Action.async { implicit request =>
    DiffForm.form.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.welcome(formWithErrors, Nil)))
      },
      userData => {
        Await.result(
          animalDao.randomAnimalWithName.map {
            case Some(id) =>
              diffDao
                .insert(Diff(id, userData.diff, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .map(_ => Redirect(routes.DiffController.get(id)))
            case _ => Future.successful(InternalServerError)
          },
          Duration(30, "s")
        )
      }
    )
  }
}
