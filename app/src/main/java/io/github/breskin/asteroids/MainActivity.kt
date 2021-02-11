package io.github.breskin.asteroids

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import io.github.breskin.asteroids.game.PowerState
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val soundManager = SoundManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Config.load(this)
        soundManager.load(this)
        Config.setMusicChangedCallback {
            soundManager.updateMusicPlayback()
        }

        setContentView(R.layout.activity_main)
        game_view.setSoundManager(soundManager)
    }

    override fun onResume() {
        super.onResume()
        enterFullscreen()
        soundManager.updateMusicPlayback()

        PowerState.Power.load(this)

        game_view.resume()
    }

    override fun onPause() {
        super.onPause()

        game_view.pause()
        soundManager.stopMusic()
    }

    override fun onDestroy() {
        super.onDestroy()

        soundManager.unload()
    }

    override fun onBackPressed() {
        if (!game_view.onBackPressed())
            super.onBackPressed()
    }

    private fun enterFullscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController

            controller?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        }
    }
}