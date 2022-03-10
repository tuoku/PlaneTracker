package com.example.planetracker.views.ar

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planetracker.models.Plane
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.SphericalUtil
import kotlin.math.absoluteValue

class ARViewModel(context: Context) : ViewModel(), SensorEventListener {

    enum class CompassCoordinateSystem { POCKET_COMPASS, REAR_CAMERA }

    val builtMarkers: MutableList<Marker> = mutableListOf()

    var _degrees = MutableLiveData<Int>()
    val degrees: LiveData<Int>
        get() = _degrees

    var _planeHeadings = MutableLiveData<Map<Double, Plane>>()
    val planeHeadings: LiveData<Map<Double, Plane>>
        get() = _planeHeadings

    var _aimedHeading = MutableLiveData<Int>()
    val aimedHeading: LiveData<Int>
        get() = _aimedHeading



    var mMap: GoogleMap? = null
    var lastKnownLocation: LatLng? = null

    private val sm: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var sRotation: Sensor = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR).also {
        sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
    }



    private var orientation3D = FloatArray(3)
    private var coordinateSystem = CompassCoordinateSystem.REAR_CAMERA

    fun compassDegrees(): Float = azimuthToDegrees(compassRadians())
    fun compassRadians(): Float = orientation3D[0]

    /** Convert such that North=0, East=90, South=180, West=270. */
    fun azimuthToDegrees(azimuth: Float): Float {
        return ((Math.toDegrees(azimuth.toDouble())+360) % 360).toFloat()
    }

    fun calculateHeadingsToPlanes(planes: List<Plane>) {
        if(lastKnownLocation == null) return
        val headings = mutableMapOf<Double, Plane>()
        planes.forEach {
            if(it.latitude != null && it.longitude != null) {
              val heading = SphericalUtil.computeHeading(
                  lastKnownLocation, LatLng(it.latitude, it.longitude)
              )
                headings[heading] = it
            }
        }
        _planeHeadings.postValue(headings)
    }

    fun findAimedPlane(map: Map<Double, Plane>?): Plane? {

        if(map == null) return null
        val degreeThreshold = 10
        val planesWithinThreshold = mutableListOf<Map.Entry<Double,Plane>>()
/*
        // get plane with smallest radian difference in headings in relation to field of view
         return (map.minByOrNull {
            (compassRadians() - it.key)
        })?.value
*/
        // filter planes to those within specified degrees of field of view

      map.forEach { entry ->

            if((entry.key.toFloat()) - Math.toDegrees(compassRadians().toDouble()) <= degreeThreshold
                && entry.value.longitude != null && entry.value.latitude != null) {
                planesWithinThreshold.add(entry)
            }
        }
        if(planesWithinThreshold.size == 0) return null




        // get the closest plane from possbile matches
        planesWithinThreshold.sortBy {
            SphericalUtil.computeDistanceBetween(
                lastKnownLocation,
                LatLng(it.value.latitude!!, it.value.longitude!!)
            )
        }

        // return null if closest match is over 50km away
        if(SphericalUtil.computeDistanceBetween(
                lastKnownLocation, LatLng(planesWithinThreshold[0].value.latitude!!,planesWithinThreshold[0].value.longitude!!))
            > 50000) {
            return null
        }
        _aimedHeading.postValue(
            (planesWithinThreshold[0].key - Math.toDegrees(compassRadians().toDouble())).toInt()
        )
        return planesWithinThreshold[0].value
    }



    override fun onSensorChanged(p0: SensorEvent?) {
        p0 ?: return

        if (p0.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, p0.values)
            when (coordinateSystem) {
                CompassCoordinateSystem.POCKET_COMPASS -> SensorManager.getOrientation(rotationMatrix, orientation3D)
                CompassCoordinateSystem.REAR_CAMERA -> {
                    val rearCameraMatrix = FloatArray(9)
                    // The axis parameters for remapCoordinateSystem() are
                    // from an example in that method's documentation
                    SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z,
                        rearCameraMatrix)
                    SensorManager.getOrientation(rearCameraMatrix, orientation3D)
                    _degrees.postValue(compassDegrees().toInt())
                    if(mMap != null) {
                        val cameraPos = mMap!!.cameraPosition
                        val pos = CameraPosition.builder(cameraPos).bearing(compassDegrees()).build() // radians to degrees
                        mMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(pos))
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d("SENSOR", p0?.name ?: "")
    }

    override fun onCleared() {
        super.onCleared()
        sm.unregisterListener(this)
    }

}