package me.martinjai.weatherapi

import cats.effect.IO
import cats.implicits._
import me.martinjai.weatherapi.Config.AppConf
import org.http4s.Method._
import org.http4s.client.Client
import org.http4s.client.dsl.io._
import org.http4s.implicits._
import models.OpenWeatherApiModels._
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder

trait WeatherService {
  def requestCurrentWeather(lat: Double, lon: Double): IO[WeatherRes]
}

object WeatherService {
  final case class WeatherError(e: Throwable) extends RuntimeException

  def impl(client: Client[IO], config: AppConf): WeatherService = new WeatherService {
    private def path(lat: Double, lon: Double) =
      uri"https://api.openweathermap.org/data/3.0/onecall?exclude=hourly,daily,minutely&units=imperial"
        .withQueryParams(
          Map(
            "lat"   -> lat.toString,
            "lon"   -> lon.toString,
            "appid" -> config.openWeatherAppId
          )
        )

    def requestCurrentWeather(lat: Double, lon: Double): IO[WeatherRes] = {
      val req = GET(path(lat, lon))
      client
        .expect[WeatherRes](req)
        .adaptError { case t => WeatherError(t) } // Prevent Client Json Decoding Failure Leaking
    }
  }
}
