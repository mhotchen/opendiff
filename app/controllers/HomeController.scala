package controllers

import play.api.mvc._
import javax.inject.Inject

import dao.DiffDao
import models.diff.Diff
import models.diff.form.DiffForm
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi

import scala.concurrent.ExecutionContext.Implicits.global

class HomeController @Inject()(val messagesApi: MessagesApi, val diffDao: DiffDao)
  extends Controller with I18nSupport
{
  def welcome = Action.async {
    diffDao.latest(40).map {
      case diffs: Seq[Diff] => Ok(views.html.welcome(DiffForm.form, diffs)).as(HTML)
      case _ => Ok(views.html.welcome(DiffForm.form, Nil)).as(HTML)
    }
  }
}
