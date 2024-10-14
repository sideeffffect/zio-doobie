package com.github.sideeffffect.scala.liquibase.doobie.zio

import com.github.sideeffffect.scala.liquibase.ScalaLiquibase
import com.zaxxer.hikari.metrics.prometheus.PrometheusHistogramMetricsTrackerFactory
import doobie.Transactor
import doobie.hikari.HikariTransactor
import zio._
import zio.interop.catz._

import java.sql.Connection

object ZIODoobieLiquibase {

  def runMigration(connection: Connection, changeLogFile: String): Task[Unit] =
    ZIO.attemptBlocking { ScalaLiquibase.runMigration(connection, changeLogFile) }

  def runMigration(transactor: Transactor[Task], changeLogFile: String): Task[Unit] = ZIO.scoped {
    transactor
      .connect(transactor.kernel)
      .toScopedZIO
      .flatMap(runMigration(_, changeLogFile))
  }

  @SuppressWarnings(Array("DisableSyntax.defaultArgs"))
  def makeTransactor(
      config: com.github.sideeffffect.scala.liquibase.doobie.Config,
      metrics: Option[PrometheusHistogramMetricsTrackerFactory] = None,
  ): RIO[Scope, Transactor[Task]] = {
    for {
      transactor <- HikariTransactor.fromConfig[Task](config.hikari, metricsTrackerFactory = metrics).toScopedZIO
      _ <- runMigration(transactor, config.liquibaseChangeLogFile)
    } yield transactor
  }

  val layer: RLayer[com.github.sideeffffect.scala.liquibase.doobie.Config, Transactor[Task]] =
    ZLayer.scoped(ZIO.service[com.github.sideeffffect.scala.liquibase.doobie.Config].flatMap(makeTransactor(_)))
}
