package models.diff.parser

sealed trait ParsedDiff

case class ParseError(error: String) extends ParsedDiff {
  override def toString = error
}

case class ValidDiff(files: List[FileDiff]) extends ParsedDiff {
  override def toString = ("" /: files) {(s, f) =>
    s + "diff %s %s\n".format(f.oldFile.name, f.newFile.name) + f
  }
}

case class FileDiff(oldFile: FileMeta, newFile: FileMeta, changeChunks: List[ChangeChunk]) {
  lazy val isAdded = (for (c <- changeChunks; l <- c.changeLines if !l.isAdded) yield l).isEmpty
  lazy val isRemoved = (for (c <- changeChunks; l <- c.changeLines if !l.isRemoved) yield l).isEmpty
  lazy val hasChangedName = oldFile.name != newFile.name

  override def toString = "--- %s\n+++ %s".format(oldFile, newFile) + ("\n" /: changeChunks) (_ + _)
}

case class ChangeChunk(rangeInformation: RangeInformation, changeLines: List[Line]) {
  override def toString = rangeInformation.toString + ("\n" /: changeLines) (_ + _ + "\n")
}

case class RangeInformation(oldOffset: Int, oldLength: Int, newOffset: Int, newLength: Int) {
  override def toString = "@@ -%d,%d +%d,%d @@".format(oldOffset, oldLength, newOffset, newLength)
}

case class FileMeta(name: String, timeStamp: String) {
  override def toString = name + (if (timeStamp != "") "\t" + timeStamp else "")
}

sealed trait Line {
  def line: String
  override def toString: String

  lazy val isAdded = isInstanceOf[LineAdded]
  lazy val isRemoved = isInstanceOf[LineRemoved]
  lazy val isContext = isInstanceOf[ContextLine]
}

case class LineRemoved(line: String) extends Line {
  override def toString = "-" + line
}

case class LineAdded(line: String) extends Line {
  override def toString = "+" + line
}

case class ContextLine(line: String) extends Line {
  override def toString = " " + line
}

object ParsedDiff {
  def apply(diff: String) = UnifiedDiffParser.parse(diff)
}
object ValidDiff {
  def apply(diff: String): ValidDiff = UnifiedDiffParser.parse(diff) match {
    case d: ValidDiff => d
    case ParseError(e) => throw new RuntimeException(e)
  }
}
