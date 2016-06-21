package models.comment.form

import models.comment.Comment
import play.api.data.Forms._
import play.api.data._

object CommentForm {
  val ignoredId: Option[Int] = None // to help the compiler out with typing

  // I'm not too concerned about validation because if someone messes with the values then the comment
  // simply doesn't appear if anything is incorrect
  val form = Form(
    mapping(
      "id" -> ignored(ignoredId),
      "comment" -> nonEmptyText(maxLength = 102400),
      "diff" -> nonEmptyText(maxLength = 102400),
      "original-file" -> nonEmptyText(maxLength = 102400),
      "changed-file" -> nonEmptyText(maxLength = 102400),
      "line-number" -> number,
      "on-original" -> boolean
    )(Comment.apply)(Comment.unapply)
  )
}
