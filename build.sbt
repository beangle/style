import BuildSettings._
import sbt._

ThisBuild / version := "0.0.2-SNAPSHOT"
ThisBuild / description := "Beangle Style Checker."
ThisBuild / organization := "org.beangle.style"
ThisBuild / homepage := Some(url("https://github.com/beangle/style"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/beangle/style"),
    "scm:git@github.com:beangle/style.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id = "duantihua",
    name = "Duan Tihua",
    email = "duantihua@163.com",
    url = url("https://github.com/duantihua")
  )
)

ThisBuild / credentials += Credentials(Path.userHome / ".sbt" / "sonatype_credentials")
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true

lazy val root = (project in file("."))
  .settings(
    name := "beangle-style",
    commonSettings
  )
  .aggregate(core, sbtplugin)

lazy val core = (project in file("core"))
  .settings(
    name := "beangle-style-core",
    commonSettings
  )

lazy val sbtplugin = (project in file("sbt"))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-beangle-style",
    commonSettings
  ).dependsOn(core)

publish / skip := true