package me.martinjai.weatherapi

import cats.effect.IO
import io.circe.syntax.EncoderOps
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io._
import models.SimpleWeatherModel.WeatherResOps

object WeatherapiRoutes {
  def weatherRoutes(weatherService: WeatherService): HttpRoutes[IO] = {
    HttpRoutes.of[IO] { case GET -> Root / "current-weather" =>
      for {
        openWeatherRes <- weatherService.get
        result = openWeatherRes.toSimpleWeatherModel
        response <- Ok(result.asJson)
      } yield response
    }
  }
}
