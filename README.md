# PlaneTracker

An app for airplane identification & tracking utilizing augmented reality for identifying planes flying overhead.

Uses [OpenSky API](https://opensky-network.org/) and [AeroDataBox API](https://www.aerodatabox.com/home).

## Features
* Watch planes fly all around the world in real time with positions derived from ADS-B and Mode S messages
* Tap a plane to see information about the plane and its flight, including an image of the actual aircraft
* Save planes as favorites and check where they're flying later
* See information about planes flying around you simply by pointing your phone at them

## Getting started
1. Clone this repository or download the .zip
2. Open in Android Studio
3. Follow all the steps outlined in the "Getting your API keys" section below
4. Run! (make sure your device has Google Play Services installed)


## Getting your API keys
### AeroDataBox API
1. Go to https://rapidapi.com/aedbx-aedbx/api/aerodatabox/
2. Log in / register to RapidAPI
3. Subscribe to AeroDataBox API (free plan works for ~200 plane taps per month per API key)
4. Copy your X-RapidAPI-Key from the API playground
5. Paste it into the `local.properties` file into a new line: `RAPID_API_KEY={your_api_key}` (without the braces)

### Google Maps API
1. Go to https://console.cloud.google.com/
2. Log in / register
3. Choose / create a project
4. Click "APIs & Services" -> "Credentials"
5. Click "Create credentials" -> "API key"
6. Copy your API key
7. Paste it into the `local.properties` file into a new line: `MAPS_API_KEY={your_api_key}` (without the braces)

### OpenSky API
1. Key not required :)


## Attributions
A big thanks to
* [@bhurling](https://gist.github.com/bhurling) for [country codes to emoji flags](https://gist.github.com/bhurling/c955c778f7a0765aaffd9214b12b3963)
* [David Leppik](https://stackoverflow.com/users/18078/david-leppik) for [remap coordinate system to account for rear camera](https://stackoverflow.com/a/53547852)
