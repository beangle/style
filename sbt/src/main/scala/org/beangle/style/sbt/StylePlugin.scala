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

import org.beangle.style.Style
import org.beangle.style.license.{LicenseOptions, Licenses}
import org.beangle.style.ws.WsOptions
import sbt.Keys.{licenses, _}
import sbt._

import java.net.URL
import scala.collection.mutable

object StylePlugin extends sbt.AutoPlugin {

  object autoImport {
    val licenseRepo = Licenses(this.getClass.getResourceAsStream("/org/beangle/style/license/detailed.md"))

    val headerLicense: SettingKey[Option[String]] = settingKey("The license to apply to files")
    val styleCheck = taskKey[Unit]("Style check")
    val styleCheckAll = taskKey[Unit]("Style check all")
    val styleFormat = taskKey[Unit]("Style format")
    val styleFormatAll = taskKey[Unit]("Style format all")

    val headerEmptyLine: SettingKey[Boolean] =
      settingKey("An empty line should be added between the header and the body")

    lazy val baseStyleSettings: Seq[Def.Setting[_]] = Seq(
      styleCheck := checkTask.value,
      styleCheckAll := styleCheck.?.all(ScopeFilter(configurations = inAnyConfiguration)).value.flatten.toSet,
      styleFormat := formatTask.value,
      styleFormatAll := styleFormat.?.all(ScopeFilter(configurations = inAnyConfiguration)).value.flatten.toSet,
      compile := compile.dependsOn(autoImport.styleCheckAll).value
    )
  }

  import autoImport._

  override def globalSettings = Vector(headerEmptyLine := true)

  override def trigger = allRequirements

  // a group of settings that are automatically added to projects.
  override val projectSettings =
    inConfig(Compile)(baseStyleSettings) ++
      inConfig(Test)(baseStyleSettings)

  lazy val formatTask =
    Def.task {
      val log = streams.value.log
      val license = detectLicense(licenses.value.toList, organizationName.value,
        startYear.value.map(_.toString), licenseRepo)
      unmanagedSourceDirectories.value.foreach { dir =>
        if (dir.exists()) {
          log.info("Style formatting in " + dir.getPath)
          Style.format(dir, None, WsOptions.Default, LicenseOptions(license, headerEmptyLine.value))
        }
      }
    }

  lazy val checkTask =
    Def.task {
      val license = detectLicense(licenses.value.toList, organizationName.value,
        startYear.value.map(_.toString), licenseRepo)
      val warns = new mutable.ArrayBuffer[String]
      val log = streams.value.log
      unmanagedSourceDirectories.value foreach { dir =>
        if (dir.exists()) {
          log.info("Style checking in " + dir.getPath)
          Style.check(dir, LicenseOptions(license, headerEmptyLine.value), warns)
        }
      }
      if (warns.nonEmpty) {
        throw new MessageOnlyException(
          s"""|Find ${warns.size} files violate style rules.!
              |  ${warns.mkString(s"\n  ")}
              |""".stripMargin
        )
      }
    }


  def detectLicense(licenses: Seq[(String, URL)], owner: String, startYear: Option[String], repos: Licenses): String = {
    val licenseName = licenses match {
      case (name, _) :: Nil => Some(name)
      case _ => None
    }
    val l = for {
      name <- licenseName
      year <- startYear
    } yield repos.license(name, year, owner)
    l.getOrElse("LICENSE NEEDED!!")
  }
}
