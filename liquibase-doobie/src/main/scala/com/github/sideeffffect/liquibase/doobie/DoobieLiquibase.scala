package com.github.sideeffffect.liquibase.doobie

import cats.effect._
import cats.effect.syntax.all._
import com.github.sideeffffect.liquibase.core.CoreLiquibase
import com.zaxxer.hikari.metrics.prometheus.PrometheusHistogramMetricsTrackerFactory
import doobie.Transactor
import doobie.hikari.HikariTransactor

import java.sql.Connection

trait DoobieLiquibase[F[_], R[_]] extends CoreLiquibase[F] {
  def runMigration(transactor: Transactor[F], changeLogFile: String): F[Unit]
  def makeTransactor(
      config: Config,
      metrics: Option[PrometheusHistogramMetricsTrackerFactory] = None,
  ): R[Transactor[F]]
}

object DoobieLiquibase {

  def apply[F[_]: Async]: DoobieLiquibase[F, Resource[F, *]] = new Apply

  class Apply[F[_]: Async] extends DoobieLiquibase[F, Resource[F, *]] {

    def runMigration(connection: Connection, changeLogFile: String): F[Unit] =
      Sync[F].blocking { CoreLiquibase.runMigration(connection, changeLogFile) }

    def runMigration(transactor: Transactor[F], changeLogFile: String): F[Unit] =
      transactor
        .connect(transactor.kernel)
        .use(runMigration(_, changeLogFile))

    @SuppressWarnings(Array("DisableSyntax.defaultArgs"))
    def makeTransactor(
        config: Config,
        metrics: Option[PrometheusHistogramMetricsTrackerFactory] = None,
    ): Resource[F, Transactor[F]] = {
      for {
        transactor <- HikariTransactor.fromConfig[F](config.hikari, metricsTrackerFactory = metrics)
        _ <- runMigration(transactor, config.liquibaseChangeLogFile).toResource
      } yield transactor
    }
  }
}
