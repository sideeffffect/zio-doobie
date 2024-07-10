import sbt._

object Dependencies {

  object Versions {
    val doobie = "1.0.0-RC5"
    val liquibase = "4.28.0"
    val zio = "2.1.6"
    val zioCats = "23.1.0.2"
    val zioConfig = "4.0.2"
  }

  val doobie = "org.tpolecat" %% "doobie-hikari" % Versions.doobie
  val liquibase = "org.liquibase" % "liquibase-core" % Versions.liquibase
  val zio = "dev.zio" %% "zio" % Versions.zio
  val zioCats = "dev.zio" %% "zio-interop-cats" % Versions.zioCats
  val zioConfig = "dev.zio" %% "zio-config-typesafe" % Versions.zioConfig
  val zioConfigMagnolia = "dev.zio" %% "zio-config-magnolia" % Versions.zioConfig

}
