# Weather Service Assignment

This is a simple weather service that provides weather information given latitude and longitude.
The service is implemented using http4s with the cats-effect library. The weather information is fetched from the
OpenWeatherMap API.

## Configuration

Obtain an API key from the OpenWeatherMap website and make sure it is enabled to make requests to One Call Api 3.0.
Create an application.conf file in the resources directory with the following content:

```
open-weather-app-id=<your-key-here>
```

## Running the service

To run the service, execute the following command:

```
sbt run
```

You can then start making requests at `localhost:8080/current-weather?lat=latitude&lon=longitude`