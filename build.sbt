import Dependencies._

name := "akkatyped"

version := "0.1"

scalaVersion := "2.12.6"

lazy val akkatyped = (project in file("."))
  .settings(
    libraryDependencies ++= akkaDependencies
  )
