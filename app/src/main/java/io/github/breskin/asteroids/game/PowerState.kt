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
        private const val MIN_SPEED_MULTIPLIER = 0.2f
        private const val MAX_BULLETS = 7
        private const val MIN_BULLET_SPRAY = 50f
        private const val MAX_BULLET_SPRAY = 110f
    }

    var scale = 1f
    var speedMultiplier = 1f
    var bulletDelay = MIN_BULLET_DELAY
    var bulletSpeedMultiplier = 1f
    var bullets = 1
    var bulletSpray = 80f

    val anglePerBullet: Float
        get() = bulletSpray / (MAX_BULLETS - 1)

    fun canUse(power: Power): Boolean {
        return when (power) {
            Power.SizeDown -> scale > MIN_SCALE
            Power.SpeedDown -> speedMultiplier > MIN_SPEED_MULTIPLIER
            Power.BulletsUp -> bullets < MAX_BULLETS
            Power.BulletSprayUp -> bulletSpray < MAX_BULLET_SPRAY
            Power.BulletSprayDown -> bulletSpray > MIN_BULLET_SPRAY
            Power.AttackSpeedUp -> bulletDelay > MIN_BULLET_DELAY
            Power.AttackSpeedDown -> bulletDelay < MAX_BULLET_DELAY

            else -> true
        }
    }

    fun apply(power: Power) {
        when (power) {
            Power.SizeUp -> scale += 0.125f
            Power.SizeDown -> if (scale > MIN_SCALE) scale -= 0.125f
            Power.SpeedDown -> if (speedMultiplier > MIN_SPEED_MULTIPLIER) speedMultiplier -= 0.2f
            Power.SpeedUp -> speedMultiplier += 0.2f
            Power.BulletsUp -> if (bullets < MAX_BULLETS) { bullets++; bulletDelay += 60 }
            Power.BulletSprayUp -> if (bulletSpray < MAX_BULLET_SPRAY) bulletSpray += 10
            Power.BulletSprayDown -> if (bulletSpray > MIN_BULLET_SPRAY) bulletSpray -= 10
            Power.AttackSpeedUp -> if (bulletDelay < MAX_BULLET_DELAY) bulletDelay -= 30
            Power.AttackSpeedDown -> if (bulletDelay > MIN_BULLET_DELAY) bulletDelay += 30
        }
    }

    fun reset() {
        scale = 1f
        speedMultiplier = 1f
        bulletDelay = MIN_BULLET_DELAY + 30 * 3
        bulletSpeedMultiplier = 1f
        bullets = 1
        bulletSpray = 80f
    }

    enum class Power {
        SizeUp, SizeDown, SpeedUp, SpeedDown, BulletsUp, BulletSprayUp, BulletSprayDown, AttackSpeedUp, AttackSpeedDown;

        companion object {
            const val AMOUNT = 9

            private lateinit var sizeUpBitmap: Bitmap
            private lateinit var sizeDownBitmap: Bitmap
            private lateinit var speedUpBitmap: Bitmap
            private lateinit var speedDownBitmap: Bitmap
            private lateinit var bulletsUpBitmap: Bitmap
            private lateinit var bulletSprayUpBitmap: Bitmap
            private lateinit var bulletSprayDownBitmap: Bitmap
            private lateinit var attackSpeedUpBitmap: Bitmap
            private lateinit var attackSpeedDownBitmap: Bitmap

            fun load(context: Context) {
                sizeUpBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.size_up)
                sizeDownBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.size_down)
                speedUpBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.speed_up)
                speedDownBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.speed_down)
                bulletsUpBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bullets_up)
                bulletSprayUpBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bullet_spray_up)
                bulletSprayDownBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bullet_spray_down)
                attackSpeedUpBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.attack_up)
                attackSpeedDownBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.attack_down)
            }

            fun get(index: Int): Power {
                return when(index) {
                    1 -> SizeDown
                    2 -> SpeedUp
                    3 -> SpeedDown
                    4 -> BulletsUp
                    5 -> BulletSprayUp
                    6 -> BulletSprayDown
                    7 -> AttackSpeedUp
                    8 -> AttackSpeedDown

                    else -> SizeUp
                }
            }

            fun random() = get(Random.nextInt(AMOUNT))
        }

        fun getBitmap(): Bitmap {
            return when (this) {
                SizeUp -> sizeUpBitmap
                SizeDown -> sizeDownBitmap
                SpeedUp -> speedUpBitmap
                SpeedDown -> speedDownBitmap
                BulletsUp -> bulletsUpBitmap
                BulletSprayUp -> bulletSprayUpBitmap
                BulletSprayDown -> bulletSprayDownBitmap
                AttackSpeedUp -> attackSpeedUpBitmap
                AttackSpeedDown -> attackSpeedDownBitmap
            }
        }

        fun getColor(alpha: Int = 255): Int {
            return when (this) {
                SizeUp, SpeedDown, AttackSpeedDown ->
                    Color.argb(alpha, 255, 0, 0)

                SizeDown, SpeedUp, BulletsUp, AttackSpeedUp ->
                    Color.argb(alpha, 0, 255, 0)

                else -> Color.argb(alpha, 0, 192, 255)
            }
        }
    }
}