package me.martinjai.weatherapi

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {
  def run:IO[Unit] = WeatherapiServer.run
}
