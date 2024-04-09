package me.martinjai.weatherapi

import cats.data.NonEmptyList
import cats.effect.IO
import me.martinjai.weatherapi.models.OpenWeatherApiModels._
import me.martinjai.weatherapi.models.SimpleWeatherModel._
import org.http4s._
import org.http4s.implicits._
import munit.CatsEffectSuite
import org.http4s.client.Client

import java.time.ZoneId

class WeatherServiceSpec extends CatsEffectSuite with munit.ClientSuite {
  private val weatherRes = WeatherRes(
    lat = 0.0,
    lon = 0.0,
    timezone = ZoneId.of("UTC"),
    timezone_offset = 0,
    alerts = List.empty,
    current = CurrentWeatherMetadata(
      temp = 0.0,
      weather = NonEmptyList.one(CurrentWeather("Clear"))
    )
  )
  private val expectedSimpleWeather = SimpleWeatherModel(
    condition = "Clear",
    feelsLike = WeatherFeelsLike.Cold,
    alerts = List.empty
  )

  private val currentWeatherReq: IO[Response[IO]] = {
    val client = Client.from { case _ =>
      IO(
        Response[IO](Status.Ok).withEntity(
          weatherRes
        )
      )
    }
    val request        = Request[IO](Method.GET, uri"/current-weather")
    val weatherService = WeatherService.impl(client)
    WeatherapiRoutes.weatherRoutes(weatherService).orNotFound(request)
  }

  test("Weather route returns status code 200") {
    val res = currentWeatherReq.map(_.status)
    assertIO(res, Status.Ok)
  }

  test("Weather route returns simple weather model") {
    val res = currentWeatherReq.flatMap(_.as[SimpleWeatherModel])
    assertIO(res, expectedSimpleWeather)
  }
}
