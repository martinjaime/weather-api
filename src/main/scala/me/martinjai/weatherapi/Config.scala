package me.martinjai.weatherapi

import cats.effect.IO
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object Config {
  final case class AppConf(openWeatherAppId: String)

  val appConf: IO[AppConf] = IO(ConfigSource.default.loadOrThrow[AppConf])
}
