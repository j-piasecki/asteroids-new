package io.github.breskin.asteroids.game

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import io.github.breskin.asteroids.R
import kotlin.random.Random

class PowerState {

    companion object {
        private const val MIN_SCALE = 0.5f
        private const val MIN_BULLET_DELAY = 240
        private const val MAX_BULLET_DELAY = 690
    }

    var scale = 1f
    var speedMultiplier = 1f
    var bulletDelay = MIN_BULLET_DELAY
    var bulletSpeedMultiplier = 1f

    fun canUse(power: Power): Boolean {
        return when (power) {
            Power.SizeDown -> scale > MIN_SCALE

            else -> true
        }
    }

    fun apply(power: Power) {
        when (power) {
            Power.SizeUp -> scale += 0.125f
            Power.SizeDown -> if (scale > MIN_SCALE) scale -= 0.125f
        }
    }

    fun reset() {
        scale = 1f
        speedMultiplier = 1f
        bulletDelay = MIN_BULLET_DELAY + 30 * 3
        bulletSpeedMultiplier = 1f
    }

    enum class Power {
        SizeUp, SizeDown;

        companion object {
            const val AMOUNT = 2

            private lateinit var sizeUpBitmap: Bitmap
            private lateinit var sizeDownBitmap: Bitmap

            fun load(context: Context) {
                sizeUpBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.size_up)
                sizeDownBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.size_down)
            }

            fun get(index: Int): Power {
                return when(index) {
                    1 -> SizeDown

                    else -> SizeUp
                }
            }

            fun random() = get(Random.nextInt(AMOUNT))
        }

        fun getBitmap(): Bitmap {
            return when (this) {
                SizeUp -> sizeUpBitmap
                SizeDown -> sizeDownBitmap
            }
        }

        fun getColor(alpha: Int = 255): Int {
            return when (this) {
                SizeUp ->
                    Color.argb(alpha, 255, 0, 0)

                SizeDown ->
                    Color.argb(alpha, 0, 255, 0)

                else -> Color.argb(alpha, 0, 192, 255)
            }
        }
    }
}