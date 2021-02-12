package io.github.breskin.asteroids.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import io.github.breskin.asteroids.*
import kotlin.math.roundToInt

class ScoreScreen(screenManager: ScreenManager) : Screen(screenManager) {

    private enum class AnimationType { Open, Close }
    private var currentAnimation = AnimationType.Open

    private val paint = Paint()

    private var score = 0
    private var gameTime = 0
    private var touchDown = false

    private var bestTime = false
    private var bestScore = false

    private var animationProgress = 0f
    private var scoreTranslation = 0f
    private var scoreAnimationProgress = 1f

    private lateinit var tapAnywhereText: String
    private lateinit var bestTimeText: String
    private lateinit var bestScoreText: String

    init {
        paint.isAntiAlias = true
    }

    override fun update() {
        updateAnimations()
    }

    override fun draw(canvas: Canvas) {
        paint.color = Color.rgb(220, 220, 220)
        paint.textSize = GameView.size * 0.1f * (scoreAnimationProgress + 1)
        canvas.drawText(score.toString(), (GameView.viewWidth - paint.measureText(score.toString())) * 0.5f, paint.textSize * (scoreAnimationProgress * 0.5f + 1) * 1.1f - scoreTranslation, paint)

        paint.color = Color.argb((animationProgress * 255).roundToInt(), 255, 255, 255)
        var margin = paint.textSize * (scoreAnimationProgress * 0.5f + 1) * 1.1f + GameView.viewHeight * 0.1f - scoreTranslation

        paint.textSize = GameView.size * 0.05f * (animationProgress + 1)
        canvas.drawText(Utils.timeToString(gameTime / 1000), (GameView.viewWidth - paint.measureText(Utils.timeToString(gameTime / 1000))) * 0.5f,
                paint.textSize + margin - GameView.size * 0.15f * (1 - animationProgress * animationProgress), paint)

        margin += paint.textSize * 1.5f

        val scoreText = if (bestScore) bestScoreText + "!" else bestScoreText + ": " + (screenManager.scoreManager?.bestScore ?: score)
        val timeText = if (bestTime) bestTimeText + "!" else bestTimeText + ": " + Utils.timeToString((screenManager.scoreManager?.bestTime ?: 0) / 1000)

        paint.textSize = GameView.size * 0.03f * (animationProgress + 1)
        canvas.drawText(timeText, (GameView.viewWidth - paint.measureText(timeText)) * 0.5f,
                paint.textSize + margin - GameView.size * 0.15f * (1 - animationProgress * animationProgress), paint)

        margin += paint.textSize * 1.3f
        canvas.drawText(scoreText, (GameView.viewWidth - paint.measureText(scoreText)) * 0.5f,
                paint.textSize + margin - GameView.size * 0.15f * (1 - animationProgress * animationProgress), paint)


        Utils.fitFontSize(paint, tapAnywhereText, GameView.size * 0.025f * (animationProgress + 1), GameView.viewWidth * 0.8f)
        canvas.drawText(tapAnywhereText, (GameView.viewWidth - paint.measureText(tapAnywhereText)) * 0.5f, GameView.viewHeight - paint.textSize * (animationProgress * 0.5f + 1), paint)
    }

    fun getScores(logic: GameLogic) {
        score = logic.score
        gameTime = logic.gameTime.roundToInt()

        bestScore = screenManager.scoreManager?.isBestScore(score) ?: false
        bestTime = screenManager.scoreManager?.isBestTime(gameTime) ?: false

        screenManager.scoreManager?.saveResult(score, gameTime)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            touchDown = true
        } else if (event.action == MotionEvent.ACTION_UP && touchDown) {
            touchDown = false

            currentAnimation = AnimationType.Close
            screenManager.target = Type.Game
        }

        return true
    }

    override fun onBackPressed(): Boolean {
        screenManager.open(Type.Home)
        return true
    }

    override fun open() {
        currentAnimation = AnimationType.Open
        touchDown = false

        scoreAnimationProgress = 1f
        animationProgress = 0f
        scoreTranslation = 0f
    }

    private fun updateAnimations() {
        when (currentAnimation) {
            AnimationType.Open -> {
                animationProgress += (1 - animationProgress) * 0.125f
            }

            AnimationType.Close -> {
                animationProgress -= animationProgress * 0.15f
                scoreAnimationProgress -= scoreAnimationProgress * 0.15f
                scoreTranslation += (GameView.size * 0.2f - scoreTranslation) * 0.1f

                if (animationProgress < 0.01f)
                    screenManager.switch()
            }
        }
    }

    override fun load(context: Context) {
        tapAnywhereText = context.getString(R.string.tap_anywhere_to_play_again)
        bestTimeText = context.getString(R.string.best_time_text)
        bestScoreText = context.getString(R.string.best_score_text)
    }
}