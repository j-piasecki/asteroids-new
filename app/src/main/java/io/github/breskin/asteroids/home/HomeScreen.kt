package io.github.breskin.asteroids.home

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.view.MotionEvent
import io.github.breskin.asteroids.*
import io.github.breskin.asteroids.controls.ToggleButton

class HomeScreen(screenManager: ScreenManager) : Screen(screenManager) {

    private enum class AnimationType {
        Opening, Closing
    }

    private var currentAnimation = AnimationType.Opening

    private val paint = Paint()

    private val buttonControls = ToggleButton { Config.oneHandedControls = it }
    private val buttonSound = ToggleButton { Config.soundEnabled = it }
    private val buttonMusic = ToggleButton { Config.musicEnabled = it }
    private val buttonVibrations = ToggleButton { Config.vibrationsEnabled = it }

    private val startButton = StartButton() {
        currentAnimation = AnimationType.Closing

        screenManager.target = Type.Game
    }

    private var togglesOffset = 0f

    init {
        paint.isAntiAlias = true
    }

    override fun update() {
        updateAnimation()

        startButton.update()

        buttonControls.update()
        buttonSound.update()
        buttonMusic.update()
        buttonVibrations.update()

        val margin = (GameView.viewWidth - buttonMusic.size * 4) / 5
        buttonVibrations.position = PointF(margin - GameView.viewWidth * 0.7f * togglesOffset, GameView.size * 0.1f)
        buttonSound.position = PointF(margin * 2 + buttonSound.size - GameView.viewWidth * 0.7f * togglesOffset * togglesOffset, GameView.size * 0.1f)
        buttonMusic.position = PointF(margin * 3 + buttonMusic.size * 2 + GameView.viewWidth * 0.7f * togglesOffset * togglesOffset, GameView.size * 0.1f)
        buttonControls.position = PointF(margin * 4 + buttonControls.size * 3 + GameView.viewWidth * 0.7f * togglesOffset, GameView.size * 0.1f)
    }

    override fun draw(canvas: Canvas) {
        buttonControls.draw(canvas)
        buttonSound.draw(canvas)
        buttonMusic.draw(canvas)
        buttonVibrations.draw(canvas)

        startButton.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        buttonControls.onTouchEvent(event)
        buttonSound.onTouchEvent(event)
        buttonMusic.onTouchEvent(event)
        buttonVibrations.onTouchEvent(event)

        startButton.onTouchEvent(event)

        return true
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun open() {
        currentAnimation = AnimationType.Opening

        startButton.startOpeningAnimation()

        togglesOffset = 1f
    }

    override fun load(context: Context) {
        buttonControls.imageOn = BitmapFactory.decodeResource(context.resources,
            R.drawable.one_handed
        )
        buttonControls.imageOff = BitmapFactory.decodeResource(context.resources,
            R.drawable.two_handed
        )
        buttonVibrations.imageOn = BitmapFactory.decodeResource(context.resources,
            R.drawable.vibrations
        )
        buttonSound.imageOn = BitmapFactory.decodeResource(context.resources, R.drawable.sound)
        buttonMusic.imageOn = BitmapFactory.decodeResource(context.resources, R.drawable.music)

        buttonControls.toggled = Config.oneHandedControls
        buttonVibrations.toggled = Config.vibrationsEnabled
        buttonSound.toggled = Config.soundEnabled
        buttonMusic.toggled = Config.musicEnabled

        startButton.text = context.getString(R.string.button_start)
    }

    override fun onSizeChanged(width: Int, height: Int) {
        super.onSizeChanged(width, height)

        startButton.resize(width, height)
    }

    fun updateAnimation() {
        when (currentAnimation) {
            AnimationType.Opening -> {
                startButton.updateOpeningAnimation()

                togglesOffset -= togglesOffset * 0.07f
            }

            AnimationType.Closing -> {
                startButton.updateClosingAnimation()

                togglesOffset += (1 - togglesOffset) * 0.1f

                if (togglesOffset > 0.975f)
                    screenManager.switch()
            }
        }
    }
}