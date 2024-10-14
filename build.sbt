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
    `liquibase-core`,
    `liquibase-doobie`,
    `liquibase-doobie-pureconfig`,
    `liquibase-doobie-zio`,
    `liquibase-doobie-zio-config`,
  )

lazy val `liquibase-core` = project
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= List(
      Dependencies.liquibase,
    ),
  )
  .enablePlugins(BuildInfoPlugin)

lazy val `liquibase-doobie` = project
  .settings(commonSettings)
  .dependsOn(`liquibase-core`)
  .settings(
    libraryDependencies ++= List(
      Dependencies.doobie,
    ),
  )
  .enablePlugins(BuildInfoPlugin)

lazy val `liquibase-doobie-pureconfig` = project
  .settings(commonSettings)
  .dependsOn(`liquibase-doobie`)
  .settings(
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

lazy val `liquibase-doobie-zio` = project
  .settings(commonSettings)
  .dependsOn(`liquibase-doobie`)
  .settings(
    libraryDependencies ++= List(
      Dependencies.zio,
      Dependencies.zioCats,
    ),
  )
  .enablePlugins(BuildInfoPlugin)

lazy val `liquibase-doobie-zio-config` = project
  .settings(commonSettings)
  .dependsOn(`liquibase-doobie`)
  .settings(
    libraryDependencies ++= List(
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
