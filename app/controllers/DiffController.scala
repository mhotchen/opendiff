package controllers

import play.api.mvc._
import javax.inject.Inject

import models.diff.form.DiffForm
import models.diff.parsers.UnifiedDiffParser
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi

class DiffController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def get(id: String) = Action {
    Ok("length: " + id).as(HTML)
  }

  def create = Action { implicit request =>
    DiffForm.form.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.welcome(formWithErrors))
      },
      userData => {
        println(userData.diff)
        Ok(views.html.diff(
          // I tried allowing the regular expressions to take carriage returns and
          // new lines using \R, \R+, \r\n, (\r)?\n, etc. but Scala wouldn't do it
          // so now I have to inefficiently go through the whole string removing
          // all carriage returns. Thanks Scala.
          // God damn that was frustrating to figure out.
          UnifiedDiffParser.parse(userData.diff + "\n"),
          userData.diff
        ))
      }
    )
  }
}
