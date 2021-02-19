package org.beangle.style.sbt

import org.beangle.style.core.LOCStat
import org.beangle.style.util.Strings
import sbt.Keys.{baseDirectory, name, streams, target}
import sbt.{Compile, Def, Test, inConfig, taskKey}

import scala.collection.mutable

object StatPlugin extends sbt.AutoPlugin {

  object autoImport {
    val statLoc = taskKey[Unit]("Stat line of code")

    lazy val baseStyleSettings: Seq[Def.Setting[_]] = Seq(
      statLoc := statLocTask.value
    )
  }

  import autoImport._

  override def trigger = allRequirements

  // a group of settings that are automatically added to projects.
  override val projectSettings =
    inConfig(Compile)(baseStyleSettings) ++
      inConfig(Test)(baseStyleSettings)

  lazy val statLocTask =
    Def.task {
      val log = streams.value.log
      val stats = new mutable.HashMap[String, Int]
      log.info("stating loc in " + baseDirectory.value)
      log.info(target.value.getAbsolutePath)
      LOCStat.countDir(baseDirectory.value, stats, Set("target"))
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
