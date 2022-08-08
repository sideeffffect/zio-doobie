import sbt._

object Dependencies {

  object Versions {
    val doobie = "1.0.0-RC2"
    val liquibase = "4.14.0"
    val zio = "1.0.16"
    val zioCats = "3.2.9.1"
    val zioConfig = "2.0.4"
  }

  val doobie = "org.tpolecat" %% "doobie-hikari" % Versions.doobie
  val liquibase = "org.liquibase" % "liquibase-core" % Versions.liquibase
  val zio = "dev.zio" %% "zio" % Versions.zio
  val zioCats = "dev.zio" %% "zio-interop-cats" % Versions.zioCats
  val zioConfig = "dev.zio" %% "zio-config-typesafe" % Versions.zioConfig
  val zioConfigMagnolia = "dev.zio" %% "zio-config-magnolia" % Versions.zioConfig

}
