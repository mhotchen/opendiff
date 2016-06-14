package models.diff

import models.diff.parsers.UnifiedDiffParser

sealed trait ParsedDiff
case class DiffError(error: String) extends ParsedDiff {
  override def toString = error
}
case class Diff(files: List[FileDiff]) extends ParsedDiff {
  override def toString = ("" /: files) {(s, f) =>
    s + "diff %s %s\n".format(f.oldFile.name, f.newFile.name) + f
  }
}

case class FileDiff(oldFile: FileMeta, newFile: FileMeta, changeChunks: List[ChangeChunk]) {
  override def toString = "--- %s\n+++ %s".format(oldFile, newFile) + ("\n" /: changeChunks) (_ + _)
}
case class FileMeta(name: String, timeStamp: String) {
  override def toString = name + (if (timeStamp != "") "\t" + timeStamp else "")
}
case class ChangeChunk(rangeInformation: RangeInformation, changeLines: List[Line]) {
  override def toString = rangeInformation.toString + ("\n" /: changeLines) (_ + _ + "\n")
}
case class RangeInformation(oldOffset: Int, oldLength: Int, newOffset: Int, newLength: Int) {
  override def toString = "@@ -%d,%d +%d,%d @@".format(oldOffset, oldLength, newOffset, newLength)
}

sealed trait Line {def line: String}
case class LineAdded(line: String) extends Line {
  override def toString = "+" + line
}
case class LineRemoved(line: String) extends Line {
  override def toString = "-" + line
}
case class ContextLine(line: String) extends Line {
  override def toString = " " + line
}

object Diff {
  def apply(diff: String) = UnifiedDiffParser.parse(diff)
}
