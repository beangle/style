/*
 * Beangle, Agile Development Scaffold and Toolkits.
 *
 * Copyright © 2005, The Beangle Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.beangle.style.ws

import org.beangle.style.util.Strings

object DefaultWhiteSpaceFormater {

  trait LineProcessor {
    def process(line: String): String
  }

  class Tab2Space(tablength: Int = 2) extends LineProcessor {
    private val spaces = " " * tablength

    override def process(line: String): String = {
      Strings.replace(line, "\t", spaces)
    }
  }

  object TrimTrailingWhiteSpace extends LineProcessor {
    override def process(line: String): String = {
      Strings.trimEnd(line)
    }
  }

  def newBuilder(): Builder = {
    new Builder
  }

  class Builder {
    var tablength = 2
    var eof = EOF.LF
    private var tab2space: Boolean = _
    private var trimTrailingWhiteSpace: Boolean = _
    private var fixLast: Boolean = _

    def enableTrimTrailingWhiteSpace(): this.type = {
      trimTrailingWhiteSpace = true
      this
    }

    def disableTrimTrailingWhiteSpace(): this.type = {
      trimTrailingWhiteSpace = false
      this
    }

    def enableTab2space(tablength: Int): this.type = {
      assert(1 <= tablength && tablength <= 8, "tablength should in [1,8]")
      this.tablength = tablength
      tab2space = true
      this
    }

    def disableTab2space(): this.type = {
      tab2space = false
      this
    }

    def insertFinalNewline(): this.type = {
      fixLast = true
      this
    }

    def fixcrlf(eof: String): this.type = {
      assert(eof == EOF.LF || eof == EOF.CRLF)
      this.eof = eof
      this
    }

    def build(): WhiteSpaceFormater = {
      val processors = new collection.mutable.ArrayBuffer[LineProcessor]
      if (tab2space) processors += new Tab2Space(tablength)
      if (trimTrailingWhiteSpace) processors += TrimTrailingWhiteSpace
      new DefaultWhiteSpaceFormater(eof, processors.toList, fixLast)
    }
  }

}

import org.beangle.style.ws.DefaultWhiteSpaceFormater._
import org.beangle.style.util.Strings

class DefaultWhiteSpaceFormater(val eof: String = EOF.LF, lineProcessors: List[LineProcessor],
                                val fixLast: Boolean)
  extends WhiteSpaceFormater {

  def format(str: String): String = {
    var fixlf = Strings.replace(str, "\r", "")
    var smaller = Strings.replace(fixlf, "\n\n\n", "\n\n")
    while (smaller.length < fixlf.length) {
      fixlf = smaller
      smaller = Strings.replace(smaller, "\n\n\n", "\n\n")
    }
    val lines = fixlf.split('\n')
    var i = 0
    while (i < lines.length) {
      var line = lines(i)
      lineProcessors foreach { p =>
        line = p.process(line)
      }
      lines(i) = line
      i += 1
    }
    join(lines, eof, fixLast)
  }

  private def join(seq: Array[String], eof: String, fixLast: Boolean): String = {
    val seqLen = seq.length
    val aim = new StringBuilder()
    (0 until seq.length) foreach { i =>
      if (i > 0) aim.append(eof)
      aim.append(seq(i))
    }
    if (fixLast) aim.append(eof)
    aim.toString
  }

}


