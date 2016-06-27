package models.diff

import java.sql.Timestamp
import java.time.LocalDateTime

import models.diff.parser.ValidDiff

case class Diff(id: Option[String], diff: String, createdAt: Timestamp) {
  lazy val parsedDiff = ValidDiff(diff)
}

object Implicits {
  implicit def localDateTimeToTimestamp(localDateTime: LocalDateTime): Timestamp =
    Timestamp.valueOf(localDateTime)

  implicit def timestampToLocalDateTime(timestamp: Timestamp): LocalDateTime =
    timestamp.toLocalDateTime
}
