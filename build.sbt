import sbt.Keys.version

onLoadMessage := s"Welcome to scalagen ${version.value}"
name := "scalagen"

import sbt._
import sbt.Keys._

lazy val scalametaV = "4.1.9"
lazy val splain = addCompilerPlugin("io.tryp" % "splain" % "0.4.1" cross CrossVersion.patch)
lazy val semanticdb = addCompilerPlugin("org.scalameta" % "semanticdb-scalac" % scalametaV cross CrossVersion.full)

lazy val sharedSettings = Seq(
  updateOptions := updateOptions.value.withCachedResolution(true),
  organization := "org.scalameta",
  version := "0.1.3-MD3",
  scalaVersion := "2.12.8",
  libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.scala-logging"  %% "scala-logging" % "3.9.0",
    "org.scalameta" %% "scalameta" % scalametaV,
    "org.typelevel" %% "cats-core" % "1.6.0",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test"
  ),
  addCompilerPlugin("io.tryp" % "splain" % "0.4.1" cross CrossVersion.patch),
  semanticdb,
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-explaintypes",
    "-feature",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    "-P:splain:all",
    "-Xcheckinit",
    "-Xfatal-warnings",
    "-Xfuture",
    "-Xlint:adapted-args",
    "-Xlint:by-name-right-associative",
    "-Xlint:constant",
    "-Xlint:delayedinit-select",
    "-Xlint:doc-detached",
    "-Xlint:inaccessible",
    "-Xlint:infer-any",
    "-Xlint:missing-interpolator",
    "-Xlint:nullary-override",
    "-Xlint:nullary-unit",
    "-Xlint:option-implicit",
    "-Xlint:package-object-classes",
    "-Xlint:poly-implicit-overload",
    "-Xlint:private-shadow",
    "-Xlint:stars-align",
    "-Xlint:type-parameter-shadow",
    "-Xlint:unsound-match",
    "-Yno-adapted-args",
    "-Ypartial-unification",
    "-Ywarn-dead-code",
    "-Ywarn-extra-implicit",
    "-Ywarn-inaccessible",
    "-Ywarn-infer-any",
    "-Ywarn-nullary-override",
    "-Ywarn-nullary-unit",
    "-Ywarn-numeric-widen",
    "-Ywarn-unused:implicits",
    "-Ywarn-unused:imports",
    "-Ywarn-unused:locals",
    "-Ywarn-unused:patvars",
    "-Ywarn-unused:privates",
    "-Ywarn-value-discard",
    "-Ycache-plugin-class-loader:last-modified",
    "-Ycache-macro-class-loader:last-modified"
  ),
  scalacOptions in (Compile, console) --= Seq("-Ywarn-unused:imports", "-Xfatal-warnings"),
  scalacOptions in (Test, console) --= Seq("-Ywarn-unused:imports", "-Xfatal-warnings"),
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
    .enablePlugins(SbtPlugin)
    .settings(sharedSettings)
    .settings(
      sbtPlugin := true,
      scriptedLaunchOpts ++= Seq("-Dplugin.version=" + version.value),
      scriptedBufferLog := false,
      moduleName := "sbt-scalagen"
    )
    .dependsOn(scalagen)
    .aggregate(scalagen)

// lazy val duplicatedFiles = Set(
//   // scalahost also provides `scalac-plugin.xml`, but we are only interested in ours.
//   "scalac-plugin.xml",
//   ".class"
// )

// lazy val compilerPluginTest =
//   project
//     .in(file("scalagen-compiler-plugin/plugin-test"))
//     .settings(sharedSettings)
//     .settings(
//       semanticdbPlugin,
//       scalacOptions in Test ++= {
//         val jar = (assembly in (testCompilerPlugin, Compile)).value
//         Seq(s"-Xplugin:${jar.getAbsolutePath}", s"-Jdummy=${jar.lastModified}")
//       }
//     )

// lazy val testCompilerPlugin =
//   project
//     .in(file("scalagen-compiler-plugin/test-plugin"))
//     .settings(sharedSettings)
//     .settings(
//       name := "scalagen-test-compiler-plugin",
//       libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided",
//       logLevel in assembly := Level.Debug,
//       assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false, includeDependency = true),
//       assemblyMergeStrategy in assembly := {
//         case x if duplicatedFiles.exists(x.endsWith) => MergeStrategy.first
//         case x => (assemblyMergeStrategy in assembly).value.apply(x)
//       },
//       skip in publish := true,
//     )
//     .dependsOn(compilerPlugin)
//     .aggregate(compilerPlugin)

// lazy val compilerPlugin =
//   project
//     .in(file("scalagen-compiler-plugin"))
//     .settings(sharedSettings)
//     .settings(Seq(
//       name := "scalagen-compiler-plugin",
//       libraryDependencies ++= Seq(
//         "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided",
//         "org.scalameta" %% "semanticdb" % scalametaV
//       ),
//     ))
//     .dependsOn(scalagen)
//     .aggregate(scalagen)
