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

import org.beangle.style.util.Files./
import org.beangle.style.util.{Charsets, Files, MediaTypes, Strings}

import java.io.{File, FileInputStream, FileOutputStream}

object EOF {
  val LF = "\n"
  val CRLF = "\r\n"
}

object WhiteSpaceFormater {

  def format(formater: WhiteSpaceFormater, dir: File, ext: Option[String]): Unit = {
    if (dir.isFile) {
      ext match {
        case Some(f) =>
          if (dir.getName.endsWith(f)) formater.format(dir)
        case None => {
          val fileExt = Strings.substringAfterLast(dir.getName, ".")

          MediaTypes.get(fileExt) foreach { m =>
            if (m.primaryType == "text" || fileExt == "xml") formater.format(dir)
          }
        }
      }
    } else {
      dir.list() foreach { childName =>
        format(formater, new File(dir.getAbsolutePath + / + childName), ext)
      }
    }
  }

  def check(dir: File, warns: collection.mutable.Buffer[String]): Unit = {
    if (dir.isFile) {
      val fileExt = Strings.substringAfterLast(dir.getName, ".")

      MediaTypes.get(fileExt) foreach { m =>
        if (m.primaryType == "text" || fileExt == "xml") {
          if (!check(dir)) {
            warns += dir.getAbsolutePath
          }
        }
      }
    } else {
      dir.list() foreach { childName =>
        check(new File(dir.getAbsolutePath + / + childName), warns)
      }
    }
  }

  private def check(file: File): Boolean = {
    val content = Files.readString(new FileInputStream(file))
    if (-1 != content.indexOf("\t") || -1 != content.indexOf(EOF.CRLF)) {
      false
    } else {
      val lines = Strings.split(content, '\n')
      lines.find(l => l.endsWith(" ")).isEmpty
    }
  }

}

trait WhiteSpaceFormater {

  def format(str: String): String

  def format(file: File): Unit = {
    val content = Files.readString(new FileInputStream(file))
    val rs = format(content)
    if (rs != content) {
      val fos = new FileOutputStream(file)
      Files.write(rs, fos, Charsets.UTF_8)
      Files.close(fos)
    }
  }
}
