package zio.doobie.liquibase

import doobie.Transactor
import doobie.hikari.HikariTransactor
import liquibase.UpdateSummaryOutputEnum
import liquibase.command.CommandScope
import liquibase.command.core.UpdateCommandStep
import liquibase.command.core.helpers.{DbUrlConnectionArgumentsCommandStep, ShowSummaryArgument}
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.ui.LoggerUIService
import zio.*
import zio.interop.catz.*

import java.sql.Connection
import scala.annotation.nowarn
import scala.jdk.CollectionConverters.*

object ZIODoobieLiquibase {

  final case class Config(
      hikari: _root_.doobie.hikari.Config,
      liquibaseChangeLogFile: String,
  )

  object Config extends ConfigVersionSpecific

  @nowarn("msg=discarded expression")
  private def runMigration(connection: Connection, changeLogFile: String): Unit = {
    import liquibase.Scope
    val db = DatabaseFactory.getInstance.findCorrectDatabaseImplementation(new JdbcConnection(connection))
    val updateCommand = new CommandScope(UpdateCommandStep.COMMAND_NAME*)
    updateCommand.addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
    updateCommand.addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, changeLogFile)
    updateCommand.addArgumentValue(ShowSummaryArgument.SHOW_SUMMARY_OUTPUT, UpdateSummaryOutputEnum.LOG)
    // https://github.com/liquibase/liquibase/issues/2396
    Scope.enter(Map[String, AnyRef](Scope.Attr.ui.name() -> new LoggerUIService()).asJava)
    val result = updateCommand.execute().getResults.asScala
    result.get("statusCode").asInstanceOf[Option[Int]] match {
      case None | Some(0) =>
      case _              => throw new RuntimeException(s"Unexpected Liquibase result: $result}")
    }
  }

  private def migrate(transactor: Transactor[Task], changeLogFile: String): Task[Unit] = ZIO.scoped {
    transactor
      .connect(transactor.kernel)
      .toScopedZIO
      .flatMap(connection => ZIO.attemptBlocking { runMigration(connection, changeLogFile) })
  }

  def make(config: Config): RIO[Scope, Transactor[Task]] = for {
    transactor <- HikariTransactor.fromConfig[Task](config.hikari).toScopedZIO
    _ <- migrate(transactor, config.liquibaseChangeLogFile)
  } yield transactor

  val layer: RLayer[Config, Transactor[Task]] =
    ZLayer.scoped(ZIO.service[Config].flatMap(make))
}
