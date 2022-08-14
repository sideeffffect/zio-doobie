import com.typesafe.tools.mima.core._

Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / turbo := true

lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .settings(
    name := "zio-doobie",
    publish / skip := true,
    mimaReportBinaryIssues := {},
  )
  .aggregate(
    zioDoobieLiquibase,
  )

lazy val zioDoobieLiquibase = project
  .in(file("zio-doobie-liquibase"))
  .settings(commonSettings)
  .settings(
    name := "zio-doobie-liquibase",
    libraryDependencies ++= List(
      Dependencies.doobie,
      Dependencies.liquibase,
      Dependencies.zio,
      Dependencies.zioCats,
      Dependencies.zioConfig,
      Dependencies.zioConfigMagnolia,
    ),
  )
  .enablePlugins(BuildInfoPlugin)

lazy val commonSettings: List[Def.Setting[_]] = DecentScala.decentScalaSettings ++ List(
  crossScalaVersions -= DecentScala.decentScalaVersion211,
  organization := "com.github.sideeffffect",
  homepage := Some(url("https://github.com/sideeffffect/zio-doobie")),
  licenses := List("APLv2" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(
      "sideeffffect",
      "Ondra Pelech",
      "ondra.pelech@gmail.com",
      url("https://github.com/sideeffffect"),
    ),
  ),
  testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
  missinglinkExcludedDependencies ++= List(
    moduleFilter(organization = "com.zaxxer", name = "HikariCP"),
    moduleFilter(organization = "dev.zio", name = "zio-interop-cats_2.12"), // depends on zio-managed
    moduleFilter(organization = "dev.zio", name = "zio-interop-cats_2.13"),
    moduleFilter(organization = "org.slf4j", name = "slf4j-api"),
  ),
  missinglinkIgnoreDestinationPackages ++= List(
    IgnoredPackage("org.osgi.framework"),
    IgnoredPackage("java.sql"), // https://github.com/tpolecat/doobie/pull/1632
  ),
  mimaBinaryIssueFilters ++= List(
  ),
)

addCommandAlias("ci", "; check; +publishLocal")
