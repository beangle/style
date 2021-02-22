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
package org.beangle.style.maven

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.{LifecyclePhase, Mojo, Parameter, ResolutionScope}
import org.apache.maven.project.MavenProject
import org.beangle.style.stat.SlocStat
import org.beangle.style.util.Strings

@Mojo(name = "sloc", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME)
class SlocMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", readonly = true)
  private var project: MavenProject = _

  def execute(): Unit = {
    val stats = new collection.mutable.HashMap[String, Int]
    SlocStat.countDir(project.getBasedir, stats, Set("target"))
    var sum = 0
    val rs = stats.toList.sortBy(_._2).reverse
    var maxLength = 0
    rs foreach {
      case (e, c) => {
        if (e.length > maxLength) maxLength = e.length
        sum += c
      }
    }

    println(s"Project has $sum lines codes.")
    rs foreach { t =>
      println(Strings.leftPad(t._1, maxLength, ' ') + "  " + t._2)
    }
  }

  override def getPluginContext: java.util.Map[_, _] = {
    super.getPluginContext
  }
}