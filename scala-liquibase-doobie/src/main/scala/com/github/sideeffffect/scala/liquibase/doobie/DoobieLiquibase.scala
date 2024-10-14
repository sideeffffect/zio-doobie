package com.github.sideeffffect.scala.liquibase.doobie

import cats.effect._
import cats.effect.syntax.all._
import com.github.sideeffffect.scala.liquibase.ScalaLiquibase
import com.zaxxer.hikari.metrics.prometheus.PrometheusHistogramMetricsTrackerFactory
import doobie.Transactor
import doobie.hikari.HikariTransactor

import java.sql.Connection

object DoobieLiquibase {

  def runMigration[F[_]: Sync](connection: Connection, changeLogFile: String): F[Unit] =
    Sync[F].blocking { ScalaLiquibase.runMigration(connection, changeLogFile) }

  def runMigration[F[_]: Sync](transactor: Transactor[F], changeLogFile: String): F[Unit] =
    transactor
      .connect(transactor.kernel)
      .use(runMigration(_, changeLogFile))

  @SuppressWarnings(Array("DisableSyntax.defaultArgs"))
  def makeTransactor[F[_]: Async](
      config: Config,
      metrics: Option[PrometheusHistogramMetricsTrackerFactory] = None,
  ): Resource[F, Transactor[F]] = {
    for {
      transactor <- HikariTransactor.fromConfig[F](config.hikari, metricsTrackerFactory = metrics)
      _ <- runMigration(transactor, config.liquibaseChangeLogFile).toResource
    } yield transactor
  }

}
