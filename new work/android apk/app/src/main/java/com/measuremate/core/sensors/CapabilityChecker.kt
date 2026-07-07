package com.measuremate.core.sensors

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import com.google.ar.core.ArCoreApk
import com.measuremate.core.ui.CapabilityStatus

data class CapabilityItem(
    val name: String,
    val status: CapabilityStatus,
    val detail: String = ""
)

class CapabilityChecker(private val context: Context) {
    private val packageManager = context.packageManager
    private val sensors = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    fun scan(): List<CapabilityItem> = listOf(
        hardware("Camera", PackageManager.FEATURE_CAMERA_ANY),
        hardware("Flash", PackageManager.FEATURE_CAMERA_FLASH),
        arCore(),
        hardware("GPS", PackageManager.FEATURE_LOCATION_GPS),
        hardware("Bluetooth", PackageManager.FEATURE_BLUETOOTH),
        sensor("Compass", Sensor.TYPE_MAGNETIC_FIELD),
        sensor("Gyroscope", Sensor.TYPE_GYROSCOPE),
        sensor("Accelerometer", Sensor.TYPE_ACCELEROMETER),
        hardware("Microphone", PackageManager.FEATURE_MICROPHONE),
        CapabilityItem("Storage", CapabilityStatus.Supported, "Private app storage and PDF sharing"),
        CapabilityItem("Internet", if (packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI) || packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) CapabilityStatus.Supported else CapabilityStatus.Limited, "Not required for offline use")
    )

    fun isArSupported(): Boolean = arCore().status == CapabilityStatus.Supported

    private fun hardware(name: String, feature: String) =
        CapabilityItem(
            name = name,
            status = if (packageManager.hasSystemFeature(feature)) CapabilityStatus.Supported else CapabilityStatus.Unavailable
        )

    private fun sensor(name: String, type: Int): CapabilityItem {
        val supported = sensors.getDefaultSensor(type) != null
        return CapabilityItem(name, if (supported) CapabilityStatus.Supported else CapabilityStatus.Unavailable)
    }

    private fun arCore(): CapabilityItem {
        val availability = ArCoreApk.getInstance().checkAvailability(context)
        return when {
            availability.isSupported -> CapabilityItem("ARCore", CapabilityStatus.Supported, "AR measurement available")
            availability.isTransient -> CapabilityItem("ARCore", CapabilityStatus.Limited, "Checking Play Services for AR")
            else -> CapabilityItem("ARCore", CapabilityStatus.Unavailable, "AR measurement will use fallback mode")
        }
    }
}
