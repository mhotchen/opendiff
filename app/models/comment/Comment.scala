package models.comment

case class Comment(
  id: Option[Int],
  comment: String,
  diffId: String,
  originalFile: String,
  changedFile: String,
  lineNumber: Int,
  onOriginal: Boolean
) {
  lazy val onChanged = !onOriginal
}
