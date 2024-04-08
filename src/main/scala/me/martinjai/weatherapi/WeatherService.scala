package me.martinjai.weatherapi

import cats.effect.IO
import cats.implicits._
import org.http4s.Method._
import org.http4s.client.Client
import org.http4s.client.dsl.io._
import org.http4s.implicits._
import me.martinjai.weatherapi.models.OpenWeatherApiModels._
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder

trait WeatherService {
  def get: IO[WeatherRes]
}

object WeatherService {
  final case class WeatherError(e: Throwable) extends RuntimeException

  private val path =
    uri"https://api.openweathermap.org/data/3.0/onecall?lat=36.1716&lon=-115.1391&exclude=hourly,daily,minutely&units=imperial&appid=d23caff1cf508fdaefbec39e17487848"

  def impl(client: Client[IO]): WeatherService = new WeatherService {
    def get: IO[WeatherRes] = {
      val req = GET(path)
      client
        .expect[WeatherRes](req)
        .adaptError { case t => WeatherError(t) } // Prevent Client Json Decoding Failure Leaking
    }
  }
}
