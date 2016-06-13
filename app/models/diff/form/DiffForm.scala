package models.diff.form

import play.api.data.Forms._
import play.api.data._

object DiffForm {
  val form = Form(
    mapping(
      "diff" -> nonEmptyText
    )(DiffFields.apply)(DiffFields.unapply)
  )
}
