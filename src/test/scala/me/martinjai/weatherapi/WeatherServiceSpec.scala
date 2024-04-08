package me.martinjai.weatherapi

import cats.data.NonEmptyList
import cats.effect.IO
import me.martinjai.weatherapi.WeatherModels.WeatherRes
import org.http4s._
import org.http4s.implicits._
import munit.CatsEffectSuite
import org.http4s.client.Client

class WeatherServiceSpec extends CatsEffectSuite with munit.ClientSuite {
  private val weatherRes = WeatherRes(
    lat = 0.0,
    lon = 0.0,
    timezone = "UTC",
    timezone_offset = 0,
    alerts = None,
    current = WeatherModels.CurrentWeatherMetadata(
      temp = 0.0,
      weather = NonEmptyList.one(WeatherModels.CurrentWeather("Clear"))
    )
  )

  private[this] val returnWeather: IO[Response[IO]] = {
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
    assertIO(returnWeather.map(_.status), Status.Ok)
  }
}
