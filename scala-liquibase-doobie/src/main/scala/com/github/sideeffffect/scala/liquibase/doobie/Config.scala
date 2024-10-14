package com.github.sideeffffect.scala.liquibase.doobie

final case class Config(
    hikari: _root_.doobie.hikari.Config,
    liquibaseChangeLogFile: String,
)
