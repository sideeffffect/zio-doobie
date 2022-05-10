package zio.doobie.liquibase

import zio.config.ConfigDescriptor
import zio.config.magnolia.DeriveConfigDescriptor
import zio.doobie.liquibase.ZIODoobieLiquibase.Config

@SuppressWarnings(Array("scalafix:DisableSyntax.valInAbstract")) // it is lazy val
trait ConfigVersionSpecific {
  implicit lazy val hikariDescriptor: ConfigDescriptor[doobie.hikari.Config] =
    DeriveConfigDescriptor.descriptor[doobie.hikari.Config]
  implicit lazy val configDescriptor: ConfigDescriptor[Config] =
    DeriveConfigDescriptor.descriptor[Config]
}
