ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .settings(
    name := "Baculima_Rafael_Mi-Vaca_Scala"
  )

libraryDependencies += "org.jfree" % "jfreechart" % "1.5.4"
libraryDependencies += "com.github.lgooddatepicker" % "LGoodDatePicker" % "11.2.1"
