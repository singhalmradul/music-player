name := "music-player"
ThisBuild / scalaVersion := "3.3.7"
ThisBuild / version := "0.1.0"
ThisBuild / organization := "be.botkop"

val openjfxVersion = "17.0.17"

libraryDependencies ++= Seq(
  "com.jfoenix" % "jfoenix" % "9.0.10",
  "org.openjfx" % "javafx-base" % openjfxVersion,
  "org.openjfx" % "javafx-controls" % openjfxVersion,
  "org.openjfx" % "javafx-media" % openjfxVersion,
  "com.mpatric" % "mp3agic" % "0.9.1"
)

scalacOptions ++= Seq(
  "-source:future",
  "-deprecation",
  "-feature"
)
