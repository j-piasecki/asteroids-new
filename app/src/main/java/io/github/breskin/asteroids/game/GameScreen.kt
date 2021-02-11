package io.github.breskin.asteroids.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import io.github.breskin.asteroids.GameView
import io.github.breskin.asteroids.Screen
import io.github.breskin.asteroids.ScreenManager
import io.github.breskin.asteroids.controls.PauseResumeButton
import kotlin.math.abs

class GameScreen(screenManager: ScreenManager) : Screen(screenManager) {

    private enum class AnimationType { None, Open, Close }
    private var currentAnimation = AnimationType.None
    private var pointsTranslation = 0f
    private var finishAnimationProgress = 0f

    private val paint = Paint()
    private val controls = Controls()
    private val pauseResumeButton = PauseResumeButton()

    private var backPressedFinishGame = false

    val logic = GameLogic(screenManager.particleSystem, screenManager.context)

    init {
        paint.isAntiAlias = true
    }

    override fun update() {
        if (backPressedFinishGame) {
            pauseResumeButton.paused = false
            logic.gamePaused = false
            logic.finishGame()

            backPressedFinishGame = false
        }

        pauseResumeButton.update(logic)

        if (logic.gamePaused)
            controls.reset()
        else
            controls.update()

        updateAnimations()

        if (currentAnimation == AnimationType.Open && (abs(logic.player.position.y) < GameView.size * 0.01 || (abs(logic.player.position.y) < GameView.size * 0.15 && controls.isMoving)))
            currentAnimation = AnimationType.None

        if (currentAnimation == AnimationType.None)
            controls.apply(logic.player)

        logic.update()

        if (logic.gameFinished && currentAnimation != AnimationType.Close) {
            screenManager.target = Type.Score
            screenManager.scoreScreen.getScores(logic)
            currentAnimation = AnimationType.Close
            controls.reset()
        }
    }

    override fun draw(canvas: Canvas) {
        logic.draw(canvas)

        controls.draw(canvas)

        pauseResumeButton.draw(canvas)
        drawPoints(canvas)
    }

    private fun drawPoints(canvas: Canvas) {
        paint.color = Color.rgb(220, 220, 220)
        paint.textSize = GameView.size * 0.1f * (finishAnimationProgress + 1)

        canvas.drawText(logic.score.toString(), (GameView.viewWidth - paint.measureText(logic.score.toString())) * 0.5f, paint.textSize * (finishAnimationProgress * 0.5f + 1) * 1.1f - pointsTranslation, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        pauseResumeButton.onTouchEvent(event)
        controls.onTouchEvent(event)

        return true
    }

    override fun onBackPressed(): Boolean {
        if (logic.gamePaused) {
            backPressedFinishGame = true
        } else {
            pauseResumeButton.paused = true
            logic.gamePaused = true
        }

        return true
    }

    override fun open() {
        currentAnimation = AnimationType.Open
        pointsTranslation = GameView.size * 0.3f
        finishAnimationProgress = 0f

        pauseResumeButton.paused = false
        pauseResumeButton.translation = GameView.size * 0.15f

        controls.reset()
        controls.startOpeningAnimation()

        logic.reset()
        logic.player.position.y = logic.space.height * 1f
        logic.player.ship.position. y = logic.space.height * 1f
    }

    override fun load(context: Context) {

    }

    override fun onSizeChanged(width: Int, height: Int) {
        super.onSizeChanged(width, height)

        logic.player.ship.size = GameView.size * 0.08f
        logic.player.position.x = 0f
        logic.player.position.y = 0f

        logic.space = Space(width * 3 / 2, height * 3 / 2)

        controls.resize(width, height)
    }

    private fun updateAnimations() {
        when (currentAnimation) {
            AnimationType.Open -> {
                controls.updateOpeningAnimation()

                logic.player.position.y += (0 - logic.player.position.y) * 0.03f

                pointsTranslation -= pointsTranslation * 0.15f
                pauseResumeButton.translation -= pauseResumeButton.translation * 0.15f
            }

            AnimationType.Close -> {
                controls.updateClosingAnimation()

                finishAnimationProgress += (1 - finishAnimationProgress) * 0.125f + 0.005f

                pauseResumeButton.translation += (GameView.size * 0.15f - pauseResumeButton.translation) * 0.15f

                if (finishAnimationProgress > 0.997f)
                    screenManager.switch()
            }
        }
    }
}