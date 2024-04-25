package zio.doobie.liquibase

import zio.config.magnolia.*

import scala.concurrent.duration.Duration
import scala.jdk.DurationConverters.*

@SuppressWarnings(Array("scalafix:DisableSyntax.valInAbstract")) // it is lazy val
trait ConfigVersionSpecific {
  private[ConfigVersionSpecific] implicit lazy val durationDeriveConfig: DeriveConfig[Duration] =
    DeriveConfig[java.time.Duration].map(_.toScala)
  implicit lazy val hikariDescriptor: DeriveConfig[_root_.doobie.hikari.Config] =
    DeriveConfig.getDeriveConfig[_root_.doobie.hikari.Config]
  implicit lazy val configDescriptor: DeriveConfig[ZIODoobieLiquibase.Config] =
    DeriveConfig.getDeriveConfig[ZIODoobieLiquibase.Config]
}
