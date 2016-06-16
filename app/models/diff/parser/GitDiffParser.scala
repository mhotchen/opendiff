package models.diff.parser

class GitDiffParser extends UnifiedDiffParser {
  override def diff: Parser[ValidDiff] = rep("""(diff.*\R(.*\R)?index.+\R)?""".r~>fileDiff) ^^ {ValidDiff(_)}

  override def oldFile: Parser[FileMeta] = "--- "~>fileName<~newline ^^ {s => FileMeta(normalizeFileName(s), "")}
  override def newFile: Parser[FileMeta] = "+++ "~>fileName<~newline ^^ {s => FileMeta(normalizeFileName(s), "")}

  private def normalizeFileName(fileName: String) = fileName.replaceFirst("""^(a|b)\/""", "")
}
