package zio.doobie.liquibase

import doobie.hikari.HikariTransactor
import doobie.{ExecutionContexts, Transactor}
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import liquibase.{Contexts, Liquibase}
import zio._
import zio.blocking.{Blocking, effectBlocking}
import zio.clock.Clock
import zio.interop.catz._

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

  private def migrate(transactor: Transactor[Task], changeLogFile: String): RIO[Blocking, Unit] = {
    transactor.connect(transactor.kernel).toManagedZIO.use { connection =>
      effectBlocking {
        runMigration(connection, changeLogFile)
      }
    }
  }

  def make(config: Config): RManaged[Blocking with Clock, Transactor[Task]] = Managed.runtime.flatMap {
    implicit r: Runtime[Blocking with Clock] =>
      for {
        // TODO: rework after https://github.com/tpolecat/doobie/pull/1690
        // HikariConfig.DEFAULT_POOL_SIZE = 10
        ce <- ExecutionContexts.fixedThreadPool[Task](config.hikari.maximumPoolSize.getOrElse(10)).toManagedZIO
        transactor <- HikariTransactor
          .fromConfig[Task](config.hikari, ce)
          .toManagedZIO
        _ <- migrate(transactor, config.liquibaseChangeLogFile).toManaged_
      } yield transactor
  }

  val layer: RLayer[Blocking with Clock with Has[Config], Has[Transactor[Task]]] =
    ZManaged.service[Config].flatMap(make).toLayer
}
