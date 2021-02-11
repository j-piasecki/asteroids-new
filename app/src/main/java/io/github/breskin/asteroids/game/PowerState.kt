package io.github.breskin.asteroids.game

import android.content.Context
import android.graphics.*
import io.github.breskin.asteroids.GameView
import io.github.breskin.asteroids.R
import io.github.breskin.asteroids.game.objects.ForceWave
import kotlin.math.roundToInt
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
        private const val INVULNERABILITY_DURATION = 10000f
        private const val BULLDOZER_DURATION = 7500f
        private const val MAX_PIERCING_BULLETS = 3
    }

    private val paint = Paint()
    private val powers = mutableListOf<Power>()

    var shipAlpha = 1f
    var scale = 1f
    var speedMultiplier = 1f
    var bulletDelay = MIN_BULLET_DELAY
    var bulletSpeedMultiplier = 1f
    var bullets = 1
    var bulletSpray = 80f
    var piercingBullets = 0

    val anglePerBullet: Float
        get() = bulletSpray / (MAX_BULLETS - 1)
    val invulnerabilityProgress: Float
        get() = invulnerabilityTime / INVULNERABILITY_DURATION
    val bulldozerProgress: Float
        get() = bulldozerTime / BULLDOZER_DURATION

    private var shieldAlpha = 0f
    private var bulldozerAlpha = 0f
    private var invulnerabilityTime = 0f
    private var bulldozerTime = 0f

    fun canUse(power: Power): Boolean {
        return when (power) {
            Power.SizeDown -> scale > MIN_SCALE
            Power.SpeedDown -> speedMultiplier > MIN_SPEED_MULTIPLIER
            Power.BulletsUp -> bullets < MAX_BULLETS
            Power.BulletSprayUp -> bulletSpray < MAX_BULLET_SPRAY
            Power.BulletSprayDown -> bulletSpray > MIN_BULLET_SPRAY
            Power.AttackSpeedUp -> bulletDelay > MIN_BULLET_DELAY
            Power.AttackSpeedDown -> bulletDelay < MAX_BULLET_DELAY
            Power.Invulnerability -> 0 > invulnerabilityTime - INVULNERABILITY_DURATION * 0.1
            Power.Bulldozer -> 0 > bulldozerTime - BULLDOZER_DURATION * 0.1
            Power.PiercingBulletsUp -> piercingBullets < MAX_PIERCING_BULLETS && piercingBullets < bullets

            else -> !powers.contains(power)
        }
    }

    fun apply(logic: GameLogic, power: Power) {
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
            Power.Invulnerability -> invulnerabilityTime = INVULNERABILITY_DURATION
            Power.Bulldozer -> bulldozerTime = BULLDOZER_DURATION
            Power.PiercingBulletsUp -> if (piercingBullets < MAX_PIERCING_BULLETS) piercingBullets++
            Power.ForceWave -> logic.space.addForceWave(logic, ForceWave(PointF(logic.player.position.x, logic.player.position.y)))

            else -> if (!powers.contains(power)) powers.add(power)
        }
    }

    fun reset() {
        shipAlpha = 1f
        scale = 1f
        speedMultiplier = 1f
        bulletDelay = MIN_BULLET_DELAY + 30 * 3
        bulletSpeedMultiplier = 1f
        bullets = 1
        bulletSpray = 80f
        piercingBullets = 0

        shieldAlpha = 0f
        bulldozerAlpha = 0f
        invulnerabilityTime = 0f
        bulldozerTime = 0f

        powers.clear()
    }

    fun update(logic: GameLogic) {
        invulnerabilityTime -= GameView.frameTime * logic.speed
        bulldozerTime -= GameView.frameTime * logic.speed

        if (hasShield() && shieldAlpha < 0.375)
            shieldAlpha += 0.035f * logic.speed
        else if (!hasShield() && shieldAlpha > 0)
            shieldAlpha -= 0.035f * logic.speed

        shieldAlpha = shieldAlpha.coerceIn(0f, 0.375f)

        if (invulnerabilityProgress > 0.95 && shipAlpha > 0.5)
            shipAlpha -= 0.027f
        else if (invulnerabilityProgress < 0.05 && shipAlpha < 1)
            shipAlpha += 0.039f

        shipAlpha = shipAlpha.coerceIn(0.5f, 1f)

        if (bulldozerProgress > 0.9 && bulldozerAlpha < 0.625)
            bulldozerAlpha += 0.027f
        else if (bulldozerProgress < 0.1 && bulldozerAlpha > 0)
            bulldozerAlpha -= 0.039f

        bulldozerAlpha = bulldozerAlpha.coerceIn(0f, 0.625f)
    }

    fun draw(canvas: Canvas, player: Player) {
        val size = player.ship.size
        var stroke = size * 0.1f / player.ship.scale
        if (stroke < 5 / player.ship.scale) stroke = 5f / player.ship.scale
        paint.strokeWidth = stroke

        canvas.save()
        canvas.translate(player.ship.position.x, player.ship.position.y)
        canvas.rotate(player.ship.rotation * 180f / Math.PI.toFloat())
        canvas.scale(player.ship.scale, player.ship.scale)

        paint.style = Paint.Style.FILL
        if (shieldAlpha > 0) {
            paint.color = Color.argb((shieldAlpha * 0.5f * 255).roundToInt(), 255, 255, 255)
            canvas.drawCircle(0f, size * 0.1f, size * 0.75f, paint)
        }

        paint.style = Paint.Style.STROKE
        if (shieldAlpha > 0) {
            paint.color = Color.argb((shieldAlpha * 255).roundToInt(), 255, 255, 255)
            canvas.drawCircle(0f, size * 0.1f, size * 0.75f, paint)
        }

        if (invulnerabilityProgress > 0) {
            val progress = ((1 - invulnerabilityProgress) * 40).coerceAtMost(1f)

            paint.color = Color.argb((shipAlpha * progress * 255).toInt(), 255, 0, 0)
            canvas.drawArc(RectF(-size * 0.65f - stroke * 2, -size * 0.55f - stroke * 2, size * 0.65f + stroke * 2, size * 0.75f + stroke * 2), 270f, -360 * invulnerabilityProgress, false, paint)
        }

        if (bulldozerProgress > 0) {
            val bulldozerSize = 60 * bulldozerProgress

            paint.color = Color.argb((bulldozerAlpha * 255).roundToInt(), 255, 255, 255)
            canvas.drawArc(RectF(-size * 0.65f - stroke * 3, -size * 0.55f - stroke * 3, size * 0.65f + stroke * 3, size * 0.75f + stroke * 3), 270f - bulldozerSize * 0.5f, bulldozerSize, false, paint)
        }

        canvas.restore()
    }

    fun hasShield() = powers.contains(Power.Shield)

    fun removeShield() = powers.remove(Power.Shield)

    enum class Power {
        SizeUp, SizeDown, SpeedUp, SpeedDown, BulletsUp, BulletSprayUp, BulletSprayDown, AttackSpeedUp, AttackSpeedDown, Shield, Invulnerability, Bulldozer, PiercingBulletsUp, ForceWave;

        companion object {
            const val AMOUNT = 14

            private lateinit var sizeUpBitmap: Bitmap
            private lateinit var sizeDownBitmap: Bitmap
            private lateinit var speedUpBitmap: Bitmap
            private lateinit var speedDownBitmap: Bitmap
            private lateinit var bulletsUpBitmap: Bitmap
            private lateinit var bulletSprayUpBitmap: Bitmap
            private lateinit var bulletSprayDownBitmap: Bitmap
            private lateinit var attackSpeedUpBitmap: Bitmap
            private lateinit var attackSpeedDownBitmap: Bitmap
            private lateinit var shieldBitmap: Bitmap
            private lateinit var invulnerabilityBitmap: Bitmap
            private lateinit var bulldozerBitmap: Bitmap
            private lateinit var piercingBulletsUpBitmap: Bitmap
            private lateinit var forceWaveBitmap: Bitmap

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
                shieldBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.shield)
                invulnerabilityBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.invulnerability)
                bulldozerBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bulldozer)
                piercingBulletsUpBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.piercing_bullets)
                forceWaveBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.force_wave)
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
                    9 -> Shield
                    10 -> Invulnerability
                    11 -> Bulldozer
                    12 -> PiercingBulletsUp
                    13 -> ForceWave

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
                Shield -> shieldBitmap
                Invulnerability -> invulnerabilityBitmap
                Bulldozer -> bulldozerBitmap
                PiercingBulletsUp -> piercingBulletsUpBitmap
                ForceWave -> forceWaveBitmap
            }
        }

        fun getColor(alpha: Int = 255): Int {
            return when (this) {
                SizeUp, SpeedDown, AttackSpeedDown ->
                    Color.argb(alpha, 255, 0, 0)

                SizeDown, SpeedUp, BulletsUp, AttackSpeedUp, PiercingBulletsUp ->
                    Color.argb(alpha, 0, 255, 0)

                Shield, Invulnerability, Bulldozer ->
                    Color.argb(alpha, 0, 255, 96);

                else -> Color.argb(alpha, 0, 192, 255)
            }
        }
    }
}