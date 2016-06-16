package models.diff.parser

import scala.util.parsing.combinator.RegexParsers

// http://stackoverflow.com/questions/3560073/how-to-write-parser-for-unified-diff-syntax
// was inspiration, although it was quite far off the mark
class UnifiedDiffParser extends RegexParsers {
  override def skipWhitespace = false

  def diff: Parser[ValidDiff] = rep("""(diff.*\R)?""".r~>fileDiff) ^^ {ValidDiff(_)}

  protected def fileDiff: Parser[FileDiff] = oldFile~newFile~rep1(changeChunk) ^^ {
    case of~nf~l => FileDiff(of, nf, l)
  }

  protected def oldFile: Parser[FileMeta] = "--- "~>(fileWithTimestamp|fileWithoutTimestamp)<~newline
  protected def newFile: Parser[FileMeta] = "+++ "~>(fileWithTimestamp|fileWithoutTimestamp)<~newline

  protected def fileWithoutTimestamp: Parser[FileMeta] = fileName ^^ {FileMeta(_, "")}
  protected def fileWithTimestamp: Parser[FileMeta] = fileName~fileNameTimestampSep~timestamp ^^ {
    case f~_~t => FileMeta(f, t)
  }

  protected def changeChunk: Parser[ChangeChunk] = rangeInfo~(newline~>rep1(line)) ^^ {
    case ri~l => ChangeChunk(ri, l)
  }

  protected def rangeInfo: Parser[RangeInformation] =
    shortLeftRangeInfo|shortRightRangeInfo|shortBothRangeInfo|fullRangeInfo

  protected def shortLeftRangeInfo: Parser[RangeInformation] = "@@ -"~>number~" +"~number~","~number<~""" @@.*""".r ^^ {
    case a~" +"~b~","~c => RangeInformation(a, a, b, c)
  }
  protected def shortRightRangeInfo: Parser[RangeInformation] = "@@ -"~>number~","~number~" +"~number<~""" @@.*""".r ^^ {
    case a~","~b~" +"~c => RangeInformation(a, b, c, c)
  }
  protected def shortBothRangeInfo: Parser[RangeInformation] = "@@ -"~>number~" +"~number<~""" @@.*""".r ^^ {
    case a~" +"~b => RangeInformation(a, a, b, b)
  }
  protected def fullRangeInfo: Parser[RangeInformation] = "@@ -"~>number~","~number~" +"~number~","~number<~""" @@.*""".r ^^ {
    case a~","~b~" +"~c~","~d => RangeInformation(a, b, c, d)
  }

  protected def line: Parser[Line] = contextLine | addedLine | deletedLine
  protected def contextLine: Parser[ContextLine] = """ .*""".r<~(newline|eof) ^^ {s => ContextLine(s.substring(1))}
  protected def addedLine: Parser[LineAdded] = """\+.*""".r<~(newline|eof) ^^ {s => LineAdded(s.substring(1))}
  protected def deletedLine: Parser[LineRemoved] = """-.*""".r<~(newline|eof) ^^ {s => LineRemoved(s.substring(1))}

  protected def fileName: Parser[String] = """.+?(?=\t|\R)""".r
  protected def timestamp: Parser[String] = """.+?(?=\t|\R)""".r
  protected def fileNameTimestampSep: Parser[String] = """\t""".r
  protected def newline: Parser[String] = """\R+""".r
  protected def eof: Parser[String] = """$""".r
  protected def number: Parser[Int] = """\d+""".r ^^ {_.toInt}
}

object UnifiedDiffParser {
  def parse(diff: String): ParsedDiff = {
    val parser = getParser(diff)
    parser.parseAll(parser.diff, diff) match {
      case parser.Success(e: ValidDiff, _) => e
      case f: parser.NoSuccess => new ParseError(f.msg)
    }
  }
  def getParser(diff: String): UnifiedDiffParser =
    if (diff.startsWith("diff --git")) new GitDiffParser else new UnifiedDiffParser
}
