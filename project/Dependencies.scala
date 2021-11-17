import sbt._

object Dependencies {

  object Versions {
    val doobie = "0.13.4"
    val liquibase = "4.6.1"
    val zio = "1.0.12"
    val zioCats = "3.1.1.0"
    val zioConfig = "1.0.10"
  }

  val doobie = "org.tpolecat" %% "doobie-hikari" % Versions.doobie
  val liquibase = "org.liquibase" % "liquibase-core" % Versions.liquibase
  val zio = "dev.zio" %% "zio" % Versions.zio
  val zioCats = "dev.zio" %% "zio-interop-cats" % Versions.zioCats
  val zioConfig = "dev.zio" %% "zio-config-typesafe" % Versions.zioConfig
  val zioConfigMagnolia = "dev.zio" %% "zio-config-magnolia" % Versions.zioConfig

}
