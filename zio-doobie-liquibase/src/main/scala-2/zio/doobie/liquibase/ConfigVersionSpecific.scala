package zio.doobie.liquibase

import zio.config.ConfigDescriptor
import zio.config.magnolia.Descriptor
import zio.doobie.liquibase.ZIODoobieLiquibase.Config

@SuppressWarnings(Array("scalafix:DisableSyntax.valInAbstract")) // it is lazy val
trait ConfigVersionSpecific {
  implicit lazy val hikariDescriptor: ConfigDescriptor[doobie.hikari.Config] =
    Descriptor.descriptor[doobie.hikari.Config]
  implicit lazy val configDescriptor: ConfigDescriptor[Config] =
    Descriptor.descriptor[Config]
}
