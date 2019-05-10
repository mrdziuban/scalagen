import sbt.Keys.version

onLoadMessage := s"Welcome to scalagen ${version.value}"
name := "scalagen"

import sbt._
import sbt.Keys._

lazy val sharedSettings: Def.SettingsDefinition = Def.settings(
  updateOptions := updateOptions.value.withCachedResolution(true),
  organization := "org.scalameta",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.8",
  libraryDependencies ++=
    "ch.qos.logback" % "logback-classic" % "1.2.3" ::
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0" ::
      "org.scalameta" %% "scalameta" % "4.1.9" ::
      "org.typelevel" %% "cats-core" % "1.6.0" ::
      "org.scalatest" %% "scalatest" % "3.0.5" % "test" :: Nil,
  scalacOptions ++=
    "-Ypartial-unification" ::
      "-Xfatal-warnings" ::
      Nil
)

lazy val scalagen =
  project
    .in(file("scalagen"))
    .settings(sharedSettings)
    .settings(name := "scalagen")

// JVM sbt plugin
lazy val sbtScalagen =
  project
    .in(file("scalagen-sbt"))
    .settings(sharedSettings, sbtPlugin := true, scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    }, scriptedBufferLog := false, moduleName := "sbt-scalagen")
    .dependsOn(scalagen)
    .enablePlugins(SbtPlugin)
