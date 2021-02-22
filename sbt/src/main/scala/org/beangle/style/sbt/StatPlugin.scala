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

package org.beangle.style.sbt

import org.beangle.style.stat.SlocStat
import org.beangle.style.util.Strings
import sbt.Keys.{baseDirectory, name, streams, target}
import sbt.{Compile, Def, Test, inConfig, taskKey}

import scala.collection.mutable

object StatPlugin extends sbt.AutoPlugin {

  object autoImport {
    val statSloc = taskKey[Unit]("Stat line of code")

    lazy val baseStyleSettings: Seq[Def.Setting[_]] = Seq(
      statSloc := statSlocTask.value
    )
  }

  import autoImport._

  override def trigger = allRequirements

  // a group of settings that are automatically added to projects.
  override val projectSettings =
    inConfig(Compile)(baseStyleSettings) ++
      inConfig(Test)(baseStyleSettings)

  lazy val statSlocTask =
    Def.task {
      val log = streams.value.log
      val stats = new mutable.HashMap[String, Int]
      log.info("stating sloc in " + baseDirectory.value)
      SlocStat.countDir(baseDirectory.value, stats, Set("target"))
      var sum = 0
      val rs = stats.toList.sortBy(_._2).reverse
      var maxLength = 0
      rs foreach {
        case (e, c) => {
          if (e.length > maxLength) maxLength = e.length
          sum += c
        }
      }

      log.info(s"${name.value} has $sum lines.")
      rs foreach { t =>
        log.info(Strings.leftPad(t._1, maxLength, ' ') + "  " + t._2)
      }
    }
}
