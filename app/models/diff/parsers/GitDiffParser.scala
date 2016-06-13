package models.diff.parsers

import models.diff._

class GitDiffParser extends UnifiedDiffParser {
  override def diff: Parser[Diff] = rep("""(diff.*\R(.*\R)?index.+\R)?""".r~>fileDiff) ^^ {Diff(_)}

  override def oldFile: Parser[FileMeta] = "--- "~>fileName<~newline ^^ {s => FileMeta(normalizeFileName(s), "")}
  override def newFile: Parser[FileMeta] = "+++ "~>fileName<~newline ^^ {s => FileMeta(normalizeFileName(s), "")}

  private def normalizeFileName(fileName: String) = fileName.replaceFirst("""^a|b\/""", "")
}
