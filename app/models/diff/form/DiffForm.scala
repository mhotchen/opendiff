package models.diff.form

import models.diff.{Diff, DiffError}
import play.api.data.Forms._
import play.api.data._
import play.api.data.validation._

object DiffForm {
  val diffConstraint: Constraint[String] = Constraint("constraints.diff") {
    plainText =>
      Diff(plainText) match {
        case DiffError(e) => Invalid(ValidationError(e))
        case Diff(_) => Valid
      }
  }

  val form = Form(
    mapping(
      "diff" -> nonEmptyText(maxLength = 102400).verifying(diffConstraint)
    )(DiffFields.apply)(DiffFields.unapply)
  )
}
