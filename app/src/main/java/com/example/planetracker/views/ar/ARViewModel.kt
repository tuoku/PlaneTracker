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

    var mMap: GoogleMap? = null
    var orientation: FloatArray? = null
    private val sm: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var sMagnetic: Sensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD).also {
        sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
    }
    private var sAccelerometer: Sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).also {
        sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
    }

    private val _magneticValue: MutableLiveData<FloatArray> = MutableLiveData()
    val magneticValue: LiveData<FloatArray> = _magneticValue

    private val _accelerometerValue: MutableLiveData<FloatArray> = MutableLiveData()
    val accelerometerValue: LiveData<FloatArray> = _accelerometerValue

    fun updateMagneticValue(value: FloatArray) {
        _magneticValue.value = value
    }

    fun updateAccelerometerValue(value: FloatArray) {
        _accelerometerValue.value = value
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        p0 ?: return
        if (p0.sensor == sMagnetic) {
            updateMagneticValue(p0.values)
            updateOrientation()
        }
        if(p0.sensor == sAccelerometer) {
            updateAccelerometerValue(p0.values)
            updateOrientation()
        }
    }

    fun updateOrientation() {
        if(accelerometerValue.value != null && magneticValue.value != null) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrix(
                rotationMatrix,
                null,
                accelerometerValue.value,
                magneticValue.value
            )

// Express the updated rotation matrix as three orientation angles.
            val orientationAngles = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)
            orientation = orientationAngles
            if (mMap != null) {
                Log.d("AZIMUTH", (orientationAngles[0] * 57.29578).toString())

                val cameraPos = mMap!!.cameraPosition
                val pos = CameraPosition.builder(cameraPos).bearing(((Math.toDegrees(
                    orientationAngles[0].toDouble()
                ) + 180.0).toFloat())).build() // radians to degrees
                mMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(pos))
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