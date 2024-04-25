package zio.doobie.liquibase

import zio.config.magnolia.*

import scala.concurrent.duration.Duration
import scala.jdk.DurationConverters.*

trait ConfigVersionSpecific {
  private[ConfigVersionSpecific] implicit lazy val durationDeriveConfig: DeriveConfig[Duration] =
    DeriveConfig[java.time.Duration].map(_.toScala)
  implicit lazy val hikariDescriptor: DeriveConfig[_root_.doobie.hikari.Config] =
    DeriveConfig.derived[_root_.doobie.hikari.Config]
  implicit lazy val configDescriptor: DeriveConfig[ZIODoobieLiquibase.Config] =
    DeriveConfig.derived[ZIODoobieLiquibase.Config]
}
