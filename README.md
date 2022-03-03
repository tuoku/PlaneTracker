# PlaneTracker

An app for airplane identification & tracking utilizing augmented reality for identifying planes flying overhead.

How to use:

First when you open the app you will see a worldmapview, and planes fying on there. When you press selected plane from the map you will see its details. Then we have a list of favourites ( You can add your own favourite planes(routes) there also.) And then we have an AR view, which is supposed to give you the details about certain plane. 
If you want to use this app you need an seperate api key.


Uses [OpenSky API](https://opensky-network.org/) and [AeroDataBox API](https://www.aerodatabox.com/home).

## Getting started
1. Clone this repository or download the .zip
2. Open in Android Studio
3. Follow all the steps outlined in the "Getting your API keys" section below
4. Run! (make sure your device has Google Play Services installed)

## Getting your API keys
### AeroDataBox API
1. Go to https://rapidapi.com/aedbx-aedbx/api/aerodatabox/
2. Log in / register to RapidAPI
3. Subscribe to AeroDataBox API (free plan works for ~60 plane taps per month per API key)
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
