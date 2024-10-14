# Liquibase Doobie

| CI | Release |
| --- | --- |
| [![Build Status][Badge-GitHubActions]][Link-GitHubActions] | [![Release Artifacts][Badge-SonatypeReleases]][Link-SonatypeReleases] |


ZIO and Cats Effect wrappers for [Doobie](https://tpolecat.github.io/doobie/) with [Liquibase](https://www.liquibase.org/).

## ZIO

```scala
"com.github.sideeffffect" %% "liquibase-doobie-zio" % "<version>"
```

This library comes with ready-made case class for configuration `Config` and a layer `ZIODoobieLiquibase.layer` that you can use when composing your application from ZLayers.

```scala
// defined in subproject `liquibase-doobie`
final case class Config(
  hikari: doobie.hikari.Config,
  liquibaseChangeLogFile: String,
)

ZIODoobieLiquibase.layer: RLayer[Config, Transactor[Task]]
```

### ZIO Config derivation

```scala
"com.github.sideeffffect" %% "liquibase-doobie-zio-config" % "<version>"
```

## Cats Effect

For Cats Effect only use

```scala
"com.github.sideeffffect" %% "liquibase-doobie" % "<version>"
```

### Pureconfig

```scala
"com.github.sideeffffect" %% "liquibase-doobie-pureconfig" % "<version>"
```


[Link-GitHubActions]: https://github.com/sideeffffect/liquibase-doobie/actions?query=workflow%3ARelease+branch%3Amaster "GitHub Actions link"
[Badge-GitHubActions]: https://github.com/sideeffffect/liquibase-doobie/workflows/Release/badge.svg?branch=master "GitHub Actions badge"

[Link-SonatypeReleases]: https://oss.sonatype.org/content/repositories/releases/com/github/sideeffffect/liquibase-doobie_2.13/ "Sonatype Releases link"
[Badge-SonatypeReleases]: https://maven-badges.herokuapp.com/maven-central/com.github.sideeffffect/liquibase-doobie_2.13/badge.svg "Sonatype Releases badge"
