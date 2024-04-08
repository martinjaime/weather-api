package me.martinjai.weatherapi

import cats.effect.IO
import com.comcast.ip4s._
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger

object WeatherapiServer {

  def run: IO[Nothing] = {
    for {
      client <- EmberClientBuilder.default[IO].build
      weatherService = WeatherService.impl(client)

      httpApp = WeatherapiRoutes.weatherRoutes(weatherService).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      _ <-
        EmberServerBuilder.default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build
    } yield ()
  }.useForever
}
