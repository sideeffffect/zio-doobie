package com.github.sideeffffect.liquibase.doobie

import _root_.pureconfig._
import _root_.pureconfig.generic.semiauto._
import doobie.enumerated.TransactionIsolation

package object pureconfig {
  private[pureconfig] implicit lazy val configConvertTransactionIsolation: ConfigConvert[TransactionIsolation] =
    deriveEnumerationConvert(ConfigFieldMapping(ScreamingSnakeCase, ScreamingSnakeCase))
  implicit lazy val configConvertHikari: ConfigConvert[_root_.doobie.hikari.Config] = deriveConvert
  implicit lazy val configConvert: ConfigConvert[Config] = deriveConvert
}
