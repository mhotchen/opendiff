package controllers

import javax.inject.Inject

import dao.CommentDao
import models.comment.form.CommentForm
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CommentController @Inject()(val messagesApi: MessagesApi, val commentDao: CommentDao)
  extends Controller with I18nSupport
{
  def create = Action.async { implicit request =>
    CommentForm.form.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(Redirect(routes.DiffController.get(formWithErrors.data("comment"))))
      },
      comment => {
        commentDao.insert(comment).map(x => Redirect(routes.DiffController.get(comment.diffId)))
      }
    )
  }
}
