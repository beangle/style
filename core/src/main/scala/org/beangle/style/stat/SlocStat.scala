package org.beangle.style.stat

import org.beangle.style.util.{Files, MediaTypes, Strings}

import java.io._
import org.beangle.style.util.Files./
import scala.collection.mutable

object SlocStat {
  def countDir(path: File, excludes: Set[String]): Map[String, Int] = {
    val stats = new mutable.HashMap[String, Int]
    countDir(path, stats, excludes)
    stats.toMap
  }

  def countDir(path: File, stats: mutable.Map[String, Int], excludes: Set[String]): Unit = {
    if (path.exists()) {
      count(path, stats, excludes)
    }
  }

  private def count(dir: File, stats: collection.mutable.Map[String, Int], excludes: Set[String]): Unit = {
    if (!dir.exists()) return;

    if (dir.getName.startsWith(".") || excludes.contains(dir.getName)) {
      return
    }
    if (dir.isFile) {
      val fileExt = Strings.substringAfterLast(dir.getName, ".")
      if (Strings.isNotBlank(fileExt) && isText(fileExt)) {
        val reader = toBufferedReader(new InputStreamReader(new FileInputStream(dir)))
        var line = reader.readLine()
        var loc = 0
        while (line != null) {
          if (Strings.isNotBlank(line)) loc += 1
          line = reader.readLine()
        }
        Files.close(reader)
        stats.get(fileExt) match {
          case Some(c) => stats.put(fileExt, c + loc)
          case None => stats.put(fileExt, loc)
        }
      }
    } else {
      dir.list() foreach { childName =>
        count(new File(dir.getAbsolutePath + / + childName), stats, excludes)
      }
    }
  }

  private def toBufferedReader(reader: Reader): BufferedReader = {
    if (reader.isInstanceOf[BufferedReader]) reader.asInstanceOf[BufferedReader] else new BufferedReader(reader)
  }

  private def isText(fileExt: String): Boolean = {
    MediaTypes.get(fileExt) match {
      case Some(m) => (m.primaryType == "text" || fileExt == "xml" || fileExt == "js")
      case None => false
    }
  }
}
