import sbt._
import Keys._

import com.typesafe.sbteclipse.plugin.EclipsePlugin._
import com.instantor.plugin.InstantorPlugin._

// ----------------------------------------------------------------------------

trait Default {
  lazy val defaultSettings =
    Defaults.defaultSettings ++
    instantorSettings ++ 
    javaSettings ++ Seq(
      organization := "com.instantor.props"
    , version      := "0.3.15-SNAPSHOT"
    , publicRelease
    , publishArtifact in Test := false
    )
}

// ----------------------------------------------------------------------------

object PropsLoaderBuild extends Build with Default {
  lazy val api = Project(
    "api"
  , file("Api")
  , settings = defaultSettings ++ Seq(
      name := "PropsLoader-Api"
    , unmanagedSourceDirectories in Test := Nil
    )
  )

  lazy val core = Project(
    "core"
  , file("Core")
  , settings = defaultSettings ++ Seq(
      name := "PropsLoader-Core"
    , libraryDependencies ++= Seq(
        slf4jApi
      , junitInterface
      , logback % "test"
      )
    , EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource
    )
  ) dependsOn(api)

  lazy val root = project in(file(".")) aggregate(api, core) settings(
    name := "PropsLoader"
  , publishLocal := {}
  , publish := {}
  )
}
