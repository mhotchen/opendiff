package models.diff

sealed trait ParsedDiff
case class DiffError(error: String) extends ParsedDiff
case class Diff(files: List[FileDiff]) extends ParsedDiff {
  override def toString = files.mkString("\n---\n")
}
case class FileDiff(oldFile: FileMeta, newFile: FileMeta, changeChunks: List[ChangeChunk])
case class FileMeta(name: String, timeStamp: String)
case class ChangeChunk(rangeInformation: RangeInformation, changeLines: List[Line])
case class RangeInformation(oldOffset: Int, oldLength: Int, newOffset: Int, newLength: Int)

sealed trait Line {def line: String}
case class LineAdded(line: String) extends Line
case class LineRemoved(line: String) extends Line
case class ContextLine(line: String) extends Line
