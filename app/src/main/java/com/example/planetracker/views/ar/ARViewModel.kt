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
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition

class ARViewModel(context: Context) : ViewModel(), SensorEventListener {

    enum class CompassCoordinateSystem { POCKET_COMPASS, REAR_CAMERA }

    var mMap: GoogleMap? = null

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