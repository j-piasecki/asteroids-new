package io.github.breskin.asteroids

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent

abstract class Screen(protected val screenManager: ScreenManager) {
    enum class Type {
        None,
        Home,
        Game,
        Score
    }

    abstract fun update()
    abstract fun draw(canvas: Canvas)
    abstract fun onTouchEvent(event: MotionEvent): Boolean
    abstract fun onBackPressed(): Boolean
    abstract fun open()
    abstract fun load(context: Context)

    open fun onSizeChanged(width: Int, height: Int) = Unit
}