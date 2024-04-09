package me.martinjai.weatherapi

import cats.effect.IO
import io.circe.syntax.EncoderOps
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io._
import models.SimpleWeatherModel.WeatherResOps

object WeatherapiRoutes {
  object LatQueryParam extends QueryParamDecoderMatcher[Double]("lat")
  object LonQueryParam extends QueryParamDecoderMatcher[Double]("lon")

  def weatherRoutes(weatherService: WeatherService): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "current-weather" :? LatQueryParam(lat) +& LonQueryParam(lon) =>
      weatherService.requestCurrentWeather(lat, lon).flatMap { openWeatherRes =>
        val result = openWeatherRes.toSimpleWeatherModel
        Ok(result.asJson)
      }
  }
}
