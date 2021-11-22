package zio.doobie.liquibase

import cats.effect.Blocker
import doobie.hikari.HikariTransactor
import doobie.{ExecutionContexts, Transactor}
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import liquibase.{Contexts, Liquibase}
import zio._
import zio.blocking.{Blocking, effectBlocking}
import zio.interop.catz._

import java.sql.Connection

object ZIODoobieLiquibase {

  final case class Config(
      url: String,
      user: String,
      password: String,
      driverClassName: String,
      threadPoolSize: Int,
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

  def make(config: Config): RManaged[Blocking, Transactor[Task]] = Managed.runtime.flatMap { implicit r: Runtime[Any] =>
    for {
      ce <- ExecutionContexts.fixedThreadPool[Task](config.threadPoolSize).toManagedZIO
      be <- Blocker[Task].toManagedZIO
      transactor <- HikariTransactor
        .newHikariTransactor[Task](config.driverClassName, config.url, config.user, config.password, ce, be)
        .toManagedZIO
      _ <- migrate(transactor, config.liquibaseChangeLogFile).toManaged_
    } yield transactor
  }

  val layer: RLayer[Blocking with Has[Config], Has[Transactor[Task]]] =
    ZManaged.service[Config].flatMap(make).toLayer
}
