package com.measuremate.core.utils

import kotlin.math.PI
import kotlin.math.pow

enum class MeasureUnit(val label: String, val meters: Double) {
    Millimeter("mm", 0.001),
    Centimeter("cm", 0.01),
    Meter("meter", 1.0),
    Inch("inch", 0.0254),
    Feet("feet", 0.3048),
    Yard("yard", 0.9144)
}

fun convert(value: Double, from: MeasureUnit, to: MeasureUnit): Double =
    value * from.meters / to.meters

data class ManualResult(
    val area: Double,
    val perimeter: Double,
    val volume: Double,
    val surfaceArea: Double
)

fun manualResult(length: Double, width: Double, height: Double): ManualResult {
    val area = length * width
    val perimeter = 2 * (length + width)
    val volume = length * width * height
    val surface = 2 * ((length * width) + (length * height) + (width * height))
    return ManualResult(area, perimeter, volume, surface)
}

object ShapeMath {
    fun rectangle(length: Double, width: Double) = length * width
    fun circle(radius: Double) = PI * radius.pow(2)
    fun triangle(base: Double, height: Double) = base * height / 2
    fun cylinderVolume(radius: Double, height: Double) = PI * radius.pow(2) * height
    fun cubeVolume(side: Double) = side.pow(3)
    fun coneVolume(radius: Double, height: Double) = PI * radius.pow(2) * height / 3
    fun sphereVolume(radius: Double) = 4 * PI * radius.pow(3) / 3
    fun paintLiters(area: Double, coats: Double, coveragePerLiter: Double) = area * coats / coveragePerLiter
    fun tileCount(area: Double, tileLength: Double, tileWidth: Double, wastePercent: Double) =
        area / (tileLength * tileWidth) * (1 + wastePercent / 100)
    fun concreteVolume(length: Double, width: Double, depth: Double) = length * width * depth
    fun brickCount(area: Double, bricksPerSquareMeter: Double, wastePercent: Double) =
        area * bricksPerSquareMeter * (1 + wastePercent / 100)
}
