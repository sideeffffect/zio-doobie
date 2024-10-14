import com.typesafe.tools.mima.core._

Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / turbo := true

lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .settings(
    publish / skip := true,
    mimaReportBinaryIssues := {},
  )
  .aggregate(
    scalaLiquibase,
    scalaLiquibaseDoobie,
    scalaLiquibaseDoobiePureconfig,
    scalaLiquibaseDoobieZio,
  )

lazy val scalaLiquibase = project
  .in(file("scala-liquibase"))
  .settings(commonSettings)
  .settings(
    name := "scala-liquibase",
    libraryDependencies ++= List(
      Dependencies.liquibase,
    ),
  )
  .enablePlugins(BuildInfoPlugin)

lazy val scalaLiquibaseDoobie = project
  .in(file("scala-liquibase-doobie"))
  .settings(commonSettings)
  .dependsOn(scalaLiquibase)
  .settings(
    name := "scala-liquibase-doobie",
    libraryDependencies ++= List(
      Dependencies.doobie,
    ),
  )
  .enablePlugins(BuildInfoPlugin)

lazy val scalaLiquibaseDoobiePureconfig = project
  .in(file("scala-liquibase-doobie-pureconfig"))
  .settings(commonSettings)
  .dependsOn(scalaLiquibaseDoobie)
  .settings(
    name := "scala-liquibase-doobie-pureconfig",
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, _)) =>
          List(Dependencies.pureconfigGeneric)
        case _ =>
          List(Dependencies.pureconfigGenericScala3)
      }
    },
  )
  .enablePlugins(BuildInfoPlugin)

lazy val scalaLiquibaseDoobieZio = project
  .in(file("scala-liquibase-doobie-zio"))
  .settings(commonSettings)
  .dependsOn(scalaLiquibaseDoobie)
  .settings(
    name := "scala-liquibase-doobie-zio",
    libraryDependencies ++= List(
      Dependencies.zio,
      Dependencies.zioCats,
      Dependencies.zioConfig,
      Dependencies.zioConfigMagnolia,
    ),
  )
  .enablePlugins(BuildInfoPlugin)

lazy val commonSettings: List[Def.Setting[_]] = DecentScala.decentScalaSettings ++ List(
  crossScalaVersions -= DecentScala.decentScalaVersion212,
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
