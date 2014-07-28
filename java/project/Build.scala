import sbt._
import Keys._

import com.typesafe.sbteclipse.plugin.EclipsePlugin._
import com.instantor.plugin.InstantorPlugin._

// ----------------------------------------------------------------------------

trait Default {
  private lazy val default =
    Defaults.defaultSettings ++
    instantorSettings ++ Seq(
      organization := "com.instantor.props"
    , version      := "0.3.11"
    )

  lazy val javaSettings =
    default ++
    publishing ++ Seq(
      EclipseKeys.projectFlavor := EclipseProjectFlavor.Java
    , autoScalaLibrary          := false
    , crossPaths                := false
    , testOptions               += Tests.Argument(TestFrameworks.JUnit, "-v", "-q")
    , unmanagedSourceDirectories in Compile := (javaSource in Compile).value :: Nil
    , unmanagedSourceDirectories in Test    := (javaSource in Test).value :: Nil
    )

  lazy val publishing = Seq(
    publishTo := Some(if (version.value endsWith "-SNAPSHOT") InstantorSnapshots else InstantorReleases)
  , javacOptions in (Compile, doc) := Nil
  , publishArtifact in Test := false
  )
}

// ----------------------------------------------------------------------------

object PropsLoaderBuild extends Build with Default {
  lazy val api = Project(
    "api"
  , file("Api")
  , settings = javaSettings ++ Seq(
      name := "PropsLoader-Api"
    , unmanagedSourceDirectories in Test := Nil
    )
  )

  lazy val core = Project(
    "core"
  , file("Core")
  , settings = javaSettings ++ Seq(
      name := "PropsLoader-Core"
    , libraryDependencies ++= Seq(
        slf4j
      , junitInterface
      , logback % "test"
      )
    , EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource
    )
  ) dependsOn(api)
}
