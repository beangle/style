package org.beangle.style.sbt

import org.beangle.style.Style
import sbt.Keys._
import sbt._

import scala.collection.mutable

object StylePlugin extends sbt.AutoPlugin {

  object autoImport {
    val styleCheck = taskKey[Unit]("Style check")
    val styleCheckAll = taskKey[Unit]("Style check all")
    val styleFormat = taskKey[Unit]("Style format")
    val styleFormatAll = taskKey[Unit]("Style format all")

    lazy val baseStyleSettings: Seq[Def.Setting[_]] = Seq(
      styleCheck := checkTask.value,
      styleCheckAll := styleCheck.?.all(ScopeFilter(configurations = inAnyConfiguration)).value.flatten.toSet,
      styleFormat := formatTask.value,
      styleFormatAll := styleFormat.?.all(ScopeFilter(configurations = inAnyConfiguration)).value.flatten.toSet,
      compile := compile.dependsOn(autoImport.styleCheckAll).value
    )
  }

  import autoImport._

  override def trigger = allRequirements

  // a group of settings that are automatically added to projects.
  override val projectSettings =
    inConfig(Compile)(baseStyleSettings) ++
      inConfig(Test)(baseStyleSettings)

  lazy val formatTask =
    Def.task {
      val log = streams.value.log
      unmanagedSourceDirectories.value.foreach { dir =>
        if (dir.exists()) {
          log.info("Style formatting in " + dir.getPath)
          Style.format(dir, None)
        }
      }
    }

  lazy val checkTask =
    Def.task {
      val warns = new mutable.ArrayBuffer[String]
      val log = streams.value.log
      unmanagedSourceDirectories.value foreach { dir =>
        if (dir.exists()) {
          log.info("Style checking in " + dir.getPath)
          Style.check(dir, warns)
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
}
