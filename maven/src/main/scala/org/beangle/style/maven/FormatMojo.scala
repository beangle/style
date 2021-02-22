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
import org.apache.maven.plugins.annotations.{Mojo, Parameter}
import org.apache.maven.project.MavenProject
import org.beangle.style.Style
import org.beangle.style.license.{LicenseOptions, Licenses}
import org.beangle.style.util.Files./
import org.beangle.style.util.Strings
import org.beangle.style.ws.WsOptions

import java.io.File

@Mojo(name = "format")
class FormatMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", readonly = true)
  private var project: MavenProject = _

  @Parameter
  private var headerEmptyLine: Boolean = true

  @Parameter
  private var license: String = _

  def execute(): Unit = {
    val dir = System.getProperty("dir")
    val ext = Option(System.getProperty("ext"))

    if (null == dir) {
      import scala.collection.JavaConverters._
      project.getCompileSourceRoots.asScala foreach { resource =>
        format(resource, ext)
      }

      project.getTestCompileSourceRoots.asScala foreach { resource =>
        format(resource, ext)
      }

      project.getResources.asScala foreach { resource =>
        format(resource.getDirectory, ext)
      }

      project.getTestResources.asScala foreach { resource =>
        format(resource.getDirectory, ext)
      }
    } else {
      if (new File(dir).exists()) {
        format(dir, ext)
      } else {
        format(project.getBasedir.getAbsolutePath + / + dir, ext)
      }
    }
  }

  private def format(path: String, ext: Option[String]): Unit = {
    if (new File(path).exists()) {
      println(s"formating $path ...")
      Style.format(new File(path), ext, WsOptions.Default, LicenseOptions(buildLicense(), headerEmptyLine))
    }
  }

  private def buildLicense(): String = {
    var l = license
    if (Strings.isEmpty(l)) {
      if (project.getLicenses.size() > 0) {
        l = project.getLicenses.get(0).getName
      } else {
        l = "License needed"
      }
    }
    val repos = Licenses(this.getClass.getResourceAsStream("/org/beangle/style/license/detailed.md"))
    repos.license(l, project.getInceptionYear, project.getOrganization.getName)
  }
}