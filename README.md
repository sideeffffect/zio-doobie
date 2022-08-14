# ZIO Doobie

| CI | Release |
| --- | --- |
| [![Build Status][Badge-GitHubActions]][Link-GitHubActions] | [![Release Artifacts][Badge-SonatypeReleases]][Link-SonatypeReleases] |

## Liquibase

ZIO wrapper for [Doobie](https://tpolecat.github.io/doobie/) with [Liquibase](https://www.liquibase.org/).

```scala
"com.github.sideeffffect" %% "zio-doobie-liquibase" % "<version>"
```

This library comes with ready-made case class for configuration `ZIODoobieLiquibase.Config` and a layer `ZIODoobieLiquibase.layer` that you can use when composing your application from ZLayers.

```scala
final case class Config(
  hikari: doobie.hikari.Config,
  liquibaseChangeLogFile: String,
)

ZIODoobieLiquibase.layer: RLayer[ZIODoobieLiquibase.Config, Transactor[Task]]
```

[Link-GitHubActions]: https://github.com/sideeffffect/zio-doobie/actions?query=workflow%3ARelease+branch%3Amaster "GitHub Actions link"
[Badge-GitHubActions]: https://github.com/sideeffffect/zio-doobie/workflows/Release/badge.svg?branch=master "GitHub Actions badge"

[Link-SonatypeReleases]: https://oss.sonatype.org/content/repositories/releases/com/github/sideeffffect/zio-doobie-liquibase_2.13/ "Sonatype Releases link"
[Badge-SonatypeReleases]: https://maven-badges.herokuapp.com/maven-central/com.github.sideeffffect/zio-doobie-liquibase_2.13/badge.svg "Sonatype Releases badge"
