package com.example.planetracker.models

class Plane(
    /**
     * Unique ICAO 24-bit address of the transponder in hex string representation.
     */
    val icao24: String,
    /**
     * Callsign of the vehicle (8 chars). Can be null if no callsign has been received.
     */
    val callsign: String?,
    /**
     * Country name inferred from the ICAO 24-bit address.
     */
    val originCountry: String,
    /**
     * Unix timestamp (seconds) for the last position update. Can be null if no position report
     * was received by OpenSky within the past 15s.
     */
    val timePosition: Double?,
    /**
     * Unix timestamp (seconds) for the last update in general. This field is updated for any new,
     * valid message received from the transponder.
     */
    val lastContact: Double,
    /**
     * WGS-84 longitude in decimal degrees. Can be null.
     */
    val longitude: Double?,
    /**
     * WGS-84 latitude in decimal degrees. Can be null.
     */
    val latitude: Double?,
    /**
     * Barometric altitude in meters. Can be null.
     */
    val baroAltitude: Double?,
    /**
     * Boolean value which indicates if the position was retrieved from a surface position report.
     */
    val onGround: Boolean,
    /**
     * Velocity over ground in m/s. Can be null.
     */
    val velocity: Double?,
    /**
     * True track in decimal degrees clockwise from north (north=0°). Can be null.
     */
    val trueTrack: Double?,
    /**
     * Vertical rate in m/s. A positive value indicates that the airplane is climbing,
     * a negative value indicates that it descends. Can be null.
     */
    val verticalRate: Double?,
    /**
     * IDs of the receivers which contributed to this state vector.
     * Is null if no filtering for sensor was used in the request.
     */
    val sensors: List<Int>?,
    /**
     * Geometric altitude in meters. Can be null.
     */
    val geoAltitude: Double?,
    /**
     * The transponder code aka Squawk. Can be null.
     */
    val squawk: String?,
    /**
     * Whether flight status indicates special purpose indicator.
     */
    val SPI: Boolean,
    /**
     * Origin of this state’s position: 0 = ADS-B, 1 = ASTERIX, 2 = MLAT
     */
    val positionSource: Double,


    ) {

}