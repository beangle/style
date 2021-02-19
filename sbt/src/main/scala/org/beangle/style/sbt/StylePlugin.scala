package org.beangle.style.sbt

import org.beangle.style.Style
import sbt.Keys._
import sbt._

import scala.collection.mutable

object StylePlugin extends sbt.AutoPlugin {

  object autoImport {
    val styleWscheck = taskKey[Unit]("Style check")
    val styleWsformat = taskKey[Unit]("Style format")

    lazy val baseStyleSettings: Seq[Def.Setting[_]] = Seq(
      styleWscheck := wsCheckTask.value,
      styleWsformat := wsFormatTask.value
    )
  }

  import autoImport._

  override def trigger = allRequirements

  // a group of settings that are automatically added to projects.
  override val projectSettings =
    inConfig(Compile)(baseStyleSettings) ++
      inConfig(Test)(baseStyleSettings)

  lazy val wsFormatTask =
    Def.task {
      val log = streams.value.log
      sourceDirectories.value foreach { dir =>
        if (dir.exists()) {
          log.info("White space formatting in " + dir.getPath)
          Style.format(dir, None)
        }
      }
    }

  lazy val wsCheckTask =
    Def.task {
      val warns = new mutable.ArrayBuffer[String]
      val log = streams.value.log
      sourceDirectories.value foreach { dir =>
        if (dir.exists()) {
          log.info("White space checking in " + dir.getPath)
          Style.check(dir, warns)
        }
      }
      if (warns.nonEmpty) {
        log.warn("Find several files violate whitespace rules.")
        warns foreach { f =>
          log.warn(f)
        }
      }
    }
}
