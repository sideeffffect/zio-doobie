package com.github.sideeffffect.scala.liquibase

import liquibase.UpdateSummaryOutputEnum
import liquibase.command.CommandScope
import liquibase.command.core.UpdateCommandStep
import liquibase.command.core.helpers.{DbUrlConnectionArgumentsCommandStep, ShowSummaryArgument}
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.ui.LoggerUIService

import java.sql.Connection
import scala.annotation.nowarn
import scala.jdk.CollectionConverters._

object ScalaLiquibase {

  @nowarn("msg=discarded expression")
  @SuppressWarnings(Array("DisableSyntax.asInstanceOf", "DisableSyntax.throw"))
  def runMigration(connection: Connection, changeLogFile: String): Unit = {
    import liquibase.Scope
    val db = DatabaseFactory.getInstance.findCorrectDatabaseImplementation(new JdbcConnection(connection))
    val updateCommand = new CommandScope(UpdateCommandStep.COMMAND_NAME*)
    val _ = updateCommand.addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
    val _ = updateCommand.addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, changeLogFile)
    val _ = updateCommand.addArgumentValue(ShowSummaryArgument.SHOW_SUMMARY_OUTPUT, UpdateSummaryOutputEnum.LOG)
    // https://github.com/liquibase/liquibase/issues/2396
    Scope.enter(Map[String, AnyRef](Scope.Attr.ui.name() -> new LoggerUIService()).asJava)
    val result = updateCommand.execute().getResults.asScala
    result.get("statusCode").asInstanceOf[Option[Int]] match {
      case None | Some(0) =>
      case _              => throw new RuntimeException(s"Unexpected Liquibase result: $result}")
    }
  }

}
