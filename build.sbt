Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / turbo := true

lazy val root = project
  .in(file("."))
  .settings(
    name := "zio-doobie",
    publish / skip := true
  )
  .aggregate(
    zioDoobieLiquibase
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
      Dependencies.zioConfigMagnolia
    )
  )
  .enablePlugins(BuildInfoPlugin)

lazy val commonSettings: List[Def.Setting[_]] = DecentScala.decentScalaSettings ++ List(
  organization := "com.github.sideeffffect",
  homepage := Some(url("https://github.com/sideeffffect/zio-doobie")),
  licenses := List("APLv2" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(
      "sideeffffect",
      "Ondra Pelech",
      "ondra.pelech@gmail.com",
      url("https://github.com/sideeffffect")
    )
  ),
  testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
  missinglinkExcludedDependencies ++= List(
    moduleFilter(organization = "org.slf4j", name = "slf4j-api"),
    moduleFilter(organization = "org.testcontainers", name = "testcontainers")
  ),
  mimaReportBinaryIssues := {}
)

addCommandAlias("ci", "; check; +publishLocal")
