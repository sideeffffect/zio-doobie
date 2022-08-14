package zio.doobie.liquibase

import doobie.hikari.HikariTransactor
import doobie.{ExecutionContexts, Transactor}
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import liquibase.{Contexts, Liquibase}
import zio.*
import zio.interop.catz.*

import java.sql.Connection

object ZIODoobieLiquibase {

  final case class Config(
      hikari: _root_.doobie.hikari.Config,
      liquibaseChangeLogFile: String,
  )

  object Config extends ConfigVersionSpecific

  private def runMigration(connection: Connection, changeLogFile: String): Unit = {
    val liquibase =
      new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), new JdbcConnection(connection))
    liquibase.update(new Contexts())
  }

  private def migrate(transactor: Transactor[Task], changeLogFile: String): Task[Unit] = ZIO.scoped {
    transactor
      .connect(transactor.kernel)
      .toScopedZIO
      .flatMap(connection => ZIO.attemptBlocking { runMigration(connection, changeLogFile) })
  }

  def make(config: Config): RIO[Scope, Transactor[Task]] = for {
    // TODO: rework after https://github.com/tpolecat/doobie/pull/1690
    // HikariConfig.DEFAULT_POOL_SIZE = 10
    ce <- ExecutionContexts.fixedThreadPool[Task](config.hikari.maximumPoolSize.getOrElse(10)).toScopedZIO
    transactor <- HikariTransactor.fromConfig[Task](config.hikari, ce).toScopedZIO
    _ <- migrate(transactor, config.liquibaseChangeLogFile)
  } yield transactor

  val layer: RLayer[Config, Transactor[Task]] =
    ZLayer.scoped(ZIO.service[Config].flatMap(make))
}
