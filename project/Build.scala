import sbt.Keys._
import sbt._

object BuildSettings {
  val scalaTest = "org.scalatest" %% "scalatest" % "3.2.5"

  val commonSettings = Seq(
    organizationName := "The Beangle Software",
    startYear := Some(2005),
    licenses += ("GNU General Public License version 3", new URL("http://www.gnu.org/licenses/lgpl-3.0.txt")),
    libraryDependencies ++= Seq(scalaTest % Test),
    crossPaths := true)
}