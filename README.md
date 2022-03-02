# PlaneTracker

An app for airplane identification & tracking utilizing augmented reality for identifying planes flying overhead.

How to use:

First when you open the app you will see a worldmapview, and planes fying on there. When you press selected plane from the map you will see its details. Then we have a list of favourites ( You can add your own favourite planes(routes) there also.) And then we have an AR view, which is supposed to give you the details about certain plane. 
If you want to use this app you need an seperate api key.


Uses [OpenSky API](https://opensky-network.org/) and [AeroDataBox API](https://www.aerodatabox.com/home).

## Getting your API keys
### AeroDataBox API
1. Go to https://rapidapi.com/aedbx-aedbx/api/aerodatabox/
2. Log in / register to RapidAPI
3. Subscribe to AeroDataBox API (free plan works for ~60 plane taps per month per API key)
4. Copy your X-RapidAPI-Key from the API playground
5. Paste it into the `local.properties` file into a new line: `RAPID_API_KEY={your_api_key}` (without the braces)
