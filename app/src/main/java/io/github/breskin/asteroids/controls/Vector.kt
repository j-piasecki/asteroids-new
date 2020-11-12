package io.github.breskin.asteroids.controls

import kotlin.math.sqrt

class Vector(var x: Float, var y: Float) {

    val length: Float
        get() = sqrt(x * x + y * y)

    fun normalize() {
        val len = length

        x /= len
        y /= len
    }
}