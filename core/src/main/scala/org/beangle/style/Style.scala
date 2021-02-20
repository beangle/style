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
package org.beangle.style

import org.beangle.style.ws.{DefaultWhiteSpaceFormater, WhiteSpaceFormater}

import java.io.File

object Style {

  def format(dir: File, ext: Option[String]): Unit = {
    val builder = DefaultWhiteSpaceFormater.newBuilder()
    builder.enableTab2space(2).enableTrimTrailingWhiteSpace().insertFinalNewline()
    WhiteSpaceFormater.format(builder.build(), dir, None)
  }

  def check(dir: File, warns: collection.mutable.Buffer[String]): Unit = {
    WhiteSpaceFormater.check(dir, warns)
  }
}
