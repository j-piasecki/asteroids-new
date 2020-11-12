package io.github.breskin.asteroids

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import io.github.breskin.asteroids.particles.ParticleSystem
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

class GameView : SurfaceView, Runnable, SurfaceHolder.Callback {

    companion object {
        var viewWidth: Int = 0
        var viewHeight: Int = 0
        var frameTime: Int = 0
        var ready = false

        val size: Int
            get() = min(viewWidth, viewHeight)
    }

    private val surfaceHolder = holder
    private lateinit var renderingThread: Thread
    private var threadRunning = false

    private val particleSystem = ParticleSystem()
    private val screenManager = ScreenManager(particleSystem)

    constructor(context: Context) : super(context) { screenManager.load(context) }
    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) { screenManager.load(context) }
    constructor(context: Context, attributes: AttributeSet, style: Int) : super(context, attributes, style) { screenManager.load(context) }

    override fun run() {
        var canvas: Canvas?

        val refreshRate = (1000 / (
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                    context.display?.refreshRate ?: 60
                else
                    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.refreshRate
                ).toFloat()).roundToInt()

        while (threadRunning) {
            if (surfaceHolder.surface.isValid) {
                val time = System.nanoTime() / 1000000

                canvas = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    surfaceHolder.lockHardwareCanvas()
                else
                    surfaceHolder.lockCanvas()

                canvas?.let {
                    ready = true

                    it.save()
                    it.drawColor(Color.BLACK)

                    particleSystem.update(screenManager.gameScreen.logic)
                    particleSystem.draw(canvas, screenManager.gameScreen.logic)

                    screenManager.update()
                    screenManager.draw(canvas)

                    it.restore()
                    surfaceHolder.unlockCanvasAndPost(it)

                    if (System.nanoTime() / 1000000 - time < refreshRate)
                        Thread.sleep(abs(refreshRate - System.nanoTime() / 1000000 + time))

                    frameTime = (System.nanoTime() / 1000000 - time).toInt()
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null)
            screenManager.onTouchEvent(event)

        return true
    }

    fun onBackPressed(): Boolean {
        return screenManager.onBackPressed()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        threadRunning = false
        renderingThread.join()
    }

    fun pause() {
        threadRunning = false
        renderingThread.join()
    }

    fun resume() {
        threadRunning = true
        renderingThread = Thread(this)
        renderingThread.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        viewWidth = w
        viewHeight = h

        surfaceHolder.setFixedSize(w, h)

        screenManager.onSizeChanged(w, h)
    }
}