package controllers

import play.api.mvc._
import javax.inject.Inject

import models.diff.form.DiffForm
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi

class HomeController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def welcome = Action {
    Ok(views.html.welcome(DiffForm.form)).as(HTML)
  }
}
