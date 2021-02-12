package io.github.breskin.asteroids

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import io.github.breskin.asteroids.game.ScoreScreen
import io.github.breskin.asteroids.game.GameScreen
import io.github.breskin.asteroids.home.HomeScreen
import io.github.breskin.asteroids.particles.ParticleSystem

class ScreenManager(val particleSystem: ParticleSystem, val context: Context) {

    private var current = Screen.Type.Home
    var target = Screen.Type.None

    var soundManager: SoundManager? = null
        set(value) {
            field = value

            gameScreen.setSoundManager(value ?: return)
        }

    var bottomSheetManager: BottomSheetManager? = null

    val homeScreen = HomeScreen(this)
    val gameScreen = GameScreen(this)
    val scoreScreen = ScoreScreen(this)

    private var started = false
    private var startDelay = 0

    fun update() {
        if (GameView.ready && !started) {
            startDelay += GameView.frameTime

            if (startDelay > 100) {
                open(Screen.Type.Home)
                started = true
            }
        }

        when (current) {
            Screen.Type.Home -> homeScreen.update()
            Screen.Type.Game -> gameScreen.update()
            Screen.Type.Score -> scoreScreen.update()
        }
    }

    fun draw(canvas: Canvas) {
        if (!started)
            return

        when (current) {
            Screen.Type.Home -> homeScreen.draw(canvas)
            Screen.Type.Game -> gameScreen.draw(canvas)
            Screen.Type.Score -> scoreScreen.draw(canvas)
        }
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        return when (current) {
            Screen.Type.Home -> homeScreen.onTouchEvent(event)
            Screen.Type.Game -> gameScreen.onTouchEvent(event)
            Screen.Type.Score -> scoreScreen.onTouchEvent(event)

            else -> true
        }
    }

    fun onBackPressed(): Boolean {
        return when (current) {
            Screen.Type.Home -> homeScreen.onBackPressed()
            Screen.Type.Game -> gameScreen.onBackPressed()
            Screen.Type.Score -> scoreScreen.onBackPressed()

            else -> false
        }
    }

    fun open(screen: Screen.Type) {
        when (screen) {
            Screen.Type.Home -> homeScreen.open()
            Screen.Type.Game -> gameScreen.open()
            Screen.Type.Score -> scoreScreen.open()
        }

        current = screen
    }

    fun switch() {
        if (target != Screen.Type.None) {
            open(target)

            target = Screen.Type.None
        }
    }

    fun load(context: Context) {
        homeScreen.load(context)
        gameScreen.load(context)
        scoreScreen.load(context)
    }

    fun onSizeChanged(width: Int, height: Int) {
        homeScreen.onSizeChanged(width, height)
        gameScreen.onSizeChanged(width, height)
        gameScreen.onSizeChanged(width, height)
    }
}