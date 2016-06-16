package models.diff

import models.diff.parser.ValidDiff

case class Diff(id: String, diff: String, createdAt: String) {
  lazy val parsedDiff = ValidDiff(diff)
}
