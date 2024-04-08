package me.martinjai.weatherapi.models

import cats.data.NonEmptyList
import cats.effect.IO
import cats.implicits._
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import org.http4s._
import org.http4s.circe._

import java.time.ZoneId

object OpenWeatherApiModels {
  implicit val zoneIdDecoder: Decoder[ZoneId] = Decoder.decodeString.emap { str =>
    Either.catchNonFatal(ZoneId.of(str)).left.map(t => s"Could not parse ZoneId: ${t.getMessage}")
  }
  implicit val zoneIdEncoder: Encoder[ZoneId] = Encoder.encodeString.contramap[ZoneId](_.toString)

  final case class WeatherRes(
      lat: Double,
      lon: Double,
      timezone: ZoneId,
      timezone_offset: Int,
      alerts: List[WeatherAlert],
      current: CurrentWeatherMetadata
  )
  object WeatherRes {
    implicit val decoder: Decoder[WeatherRes]                 = deriveDecoder[WeatherRes]
    implicit val entityDecoder: EntityDecoder[IO, WeatherRes] = jsonOf
    implicit val encoder: Encoder[WeatherRes]                 = deriveEncoder[WeatherRes]
    implicit val entityEncoder: EntityEncoder[IO, WeatherRes] = jsonEncoderOf
  }

  final case class CurrentWeatherMetadata(temp: Double, weather: NonEmptyList[CurrentWeather])
  object CurrentWeatherMetadata {
    implicit val decoder: Decoder[CurrentWeatherMetadata] = deriveDecoder[CurrentWeatherMetadata]
    implicit val entityDecoder: EntityDecoder[IO, CurrentWeatherMetadata] = jsonOf
    implicit val encoder: Encoder[CurrentWeatherMetadata] = deriveEncoder[CurrentWeatherMetadata]
    implicit val entityEncoder: EntityEncoder[IO, CurrentWeatherMetadata] = jsonEncoderOf
  }

  final case class CurrentWeather(description: String)
  object CurrentWeather {
    implicit val decoder: Decoder[CurrentWeather]                 = deriveDecoder[CurrentWeather]
    implicit val entityDecoder: EntityDecoder[IO, CurrentWeather] = jsonOf
    implicit val encoder: Encoder[CurrentWeather]                 = deriveEncoder[CurrentWeather]
    implicit val entityEncoder: EntityEncoder[IO, CurrentWeather] = jsonEncoderOf
  }

  // Need a decoder because OpenWeather api will return a nullable value for alerts
  implicit val weatherAlertListDecoder: Decoder[List[WeatherAlert]] =
    Decoder.decodeOption(Decoder.decodeList[WeatherAlert]).map(_.getOrElse(List.empty))

  final case class WeatherAlert(description: String)
  object WeatherAlert {
    implicit val decoder: Decoder[WeatherAlert]                 = deriveDecoder[WeatherAlert]
    implicit val entityDecoder: EntityDecoder[IO, WeatherAlert] = jsonOf
    implicit val encoder: Encoder[WeatherAlert]                 = deriveEncoder[WeatherAlert]
    implicit val entityEncoder: EntityEncoder[IO, WeatherAlert] = jsonEncoderOf
  }
}
