package zio.doobie.liquibase

import zio.config.ConfigDescriptor
import zio.config.magnolia.Descriptor
import zio.doobie.liquibase.ZIODoobieLiquibase.Config

trait ConfigVersionSpecific {
  implicit lazy val hikariDescriptor: ConfigDescriptor[doobie.hikari.Config] =
    Descriptor.derived[doobie.hikari.Config].desc
  implicit lazy val configDescriptor: ConfigDescriptor[Config] =
    Descriptor.derived[Config].desc
}
