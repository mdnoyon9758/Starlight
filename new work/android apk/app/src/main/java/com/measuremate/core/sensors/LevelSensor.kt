package com.measuremate.core.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt

data class LevelReading(
    val xAngle: Int = 0,
    val yAngle: Int = 0,
    val slope: Int = 0,
    val compass: Int? = null,
    val supported: Boolean = false
)

class LevelSensor(context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    fun readings(): Flow<LevelReading> = callbackFlow {
        if (accelerometer == null) {
            trySend(LevelReading(supported = false))
            close()
            return@callbackFlow
        }
        val gravity = FloatArray(3)
        val magnetic = FloatArray(3)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        System.arraycopy(event.values, 0, gravity, 0, 3)
                        val x = Math.toDegrees(atan2(gravity[0].toDouble(), sqrt((gravity[1] * gravity[1] + gravity[2] * gravity[2]).toDouble()))).roundToInt()
                        val y = Math.toDegrees(atan2(gravity[1].toDouble(), sqrt((gravity[0] * gravity[0] + gravity[2] * gravity[2]).toDouble()))).roundToInt()
                        trySend(LevelReading(x, y, maxOf(kotlin.math.abs(x), kotlin.math.abs(y)), compass = bearing(gravity, magnetic), supported = true))
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> System.arraycopy(event.values, 0, magnetic, 0, 3)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        if (magnetometer != null) sensorManager.registerListener(listener, magnetometer, SensorManager.SENSOR_DELAY_UI)
        awaitClose { sensorManager.unregisterListener(listener) }
    }

    private fun bearing(gravity: FloatArray, magnetic: FloatArray): Int? {
        if (magnetic.all { it == 0f }) return null
        val rotation = FloatArray(9)
        val orientation = FloatArray(3)
        if (!SensorManager.getRotationMatrix(rotation, null, gravity, magnetic)) return null
        SensorManager.getOrientation(rotation, orientation)
        val degrees = Math.toDegrees(orientation[0].toDouble()).roundToInt()
        return (degrees + 360) % 360
    }
}
