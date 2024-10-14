package com.github.sideeffffect.scala.liquibase.doobie.zio

import _root_.zio.config.magnolia.*

import scala.concurrent.duration.Duration
import scala.jdk.DurationConverters.*

package object config {
  private[config] implicit lazy val durationDeriveConfig: DeriveConfig[Duration] =
    DeriveConfig[java.time.Duration].map(_.toScala)
  implicit lazy val hikariDescriptor: DeriveConfig[_root_.doobie.hikari.Config] =
    DeriveConfig.derived
  implicit lazy val configDescriptor: DeriveConfig[com.github.sideeffffect.scala.liquibase.doobie.Config] =
    DeriveConfig.derived
}
