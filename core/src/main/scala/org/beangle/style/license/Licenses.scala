/*
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

package org.beangle.style.license

import org.beangle.style.util.Strings

import java.io.InputStream
import scala.collection.mutable
import scala.io.Codec

object Licenses {
  def apply(input: InputStream): Licenses = {
    val templates = new mutable.HashMap[String, String]

    val content =
      try scala.io.Source.fromInputStream(input)(Codec.UTF8).mkString
      finally input.close()
    val licenses = Strings.split(content, '#')
    licenses foreach { license =>
      var name = license.substring(0, license.indexOf('\n'))
      val body = license.substring(name.length).trim
      if (name.contains("(")) {
        val shortName = Strings.substringBetween(name, "(", ")")
        name = Strings.substringBefore(name, "(")
        templates.put(name.trim, body)
        templates.put(shortName.trim, body)
      } else {
        templates.put(name.trim, body)
      }
    }
    new Licenses(templates.toMap)
  }
}

class Licenses(templates: Map[String, String]) {

  def get(name: String): Option[String] = {
    templates.get(name)
  }

  def license(name: String, year: String, owner: String): String = {
    var content = templates.getOrElse(name, name)
    content = Strings.replace(content, "${year}", year)
    content = Strings.replace(content, "${owner}", owner)
    content
  }

}
