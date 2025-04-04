# ShURL
A simple ktor-based redirection server based on SQLite

### Configuration
The following environmental variables are used for configuration of ShURL instance:
* `SHURL_PORT` - decides on which port should ShURL bind, defaults to `8080`
* `SHURL_HOST` - decided for which host should ShURL bind, defaults to `0.0.0.0`
* `SHURL_NAME` - decides what name should be used in frontend tab title