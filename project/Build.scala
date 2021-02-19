import sbt.Keys._
import sbt._

object BuildSettings {
  val commonSettings = Seq(
    organizationName  := "Beangle",
    startYear := Some(2005),
    licenses += ("LGPL-3.0", new URL("http://www.gnu.org/licenses/lgpl-3.0.txt")),
    crossPaths := true)
}