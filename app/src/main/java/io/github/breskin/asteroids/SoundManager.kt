package io.github.breskin.asteroids

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

class SoundManager {
    enum class SoundEffect { Shoot, Crash, ShieldPop, Explode, ForceWave }

    private lateinit var musicSoundPool: SoundPool
    private lateinit var effectsSoundPool: SoundPool

    private var musicSoundId: Int = 0
    private var shootSoundId: Int = 0
    private var crashSoundId: Int = 0
    private var shieldPopSoundId: Int = 0
    private var explodeSoundId: Int = 0
    private var forceWaveSoundId: Int = 0

    private var musicPlaybackId = -1

    fun playSound(effect: SoundEffect, volume: Float = 0.5f) {
        if (!Config.soundEnabled)
            return

        var priority = 0
        var id = 0

        when (effect) {
            SoundEffect.Shoot -> { id = shootSoundId; priority = 1 }
            SoundEffect.Crash -> { id = crashSoundId; priority = 0 }
            SoundEffect.ShieldPop -> { id = shieldPopSoundId; priority = 2 }
            SoundEffect.Explode -> { id = explodeSoundId; priority = 3 }
            SoundEffect.ForceWave -> { id = forceWaveSoundId; priority = 2 }
        }

        effectsSoundPool.play(id, volume, volume, priority, 0, 1f)
    }

    fun updateMusicPlayback() {
        if (Config.musicEnabled && musicPlaybackId == -1) {
            musicPlaybackId = musicSoundPool.play(musicSoundId, 0.6f, 0.6f, 1, -1, 1f)
        } else if (!Config.musicEnabled && musicPlaybackId != -1) {
            stopMusic()
        }
    }

    fun stopMusic() {
        musicSoundPool.stop(musicPlaybackId)
        musicPlaybackId = -1
    }

    fun load(context: Context) {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        effectsSoundPool = SoundPool.Builder().setMaxStreams(8).setAudioAttributes(attributes).build()

        shootSoundId = effectsSoundPool.load(context, R.raw.shoot, 1)
        crashSoundId = effectsSoundPool.load(context, R.raw.crash, 1)
        shieldPopSoundId = effectsSoundPool.load(context, R.raw.shield_pop, 1)
        explodeSoundId = effectsSoundPool.load(context, R.raw.explosion, 1)
        forceWaveSoundId = effectsSoundPool.load(context, R.raw.force_wave, 1)

        musicSoundPool = SoundPool.Builder().setMaxStreams(1).setAudioAttributes(attributes).build()
        musicSoundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
            if (status == 0 && Config.musicEnabled)
                musicPlaybackId = musicSoundPool.play(musicSoundId, 0.6f, 0.6f, 1, -1, 1f)
        }

        musicSoundId = musicSoundPool.load(context, R.raw.music, 1)
    }

    fun unload() {
        musicSoundPool.unload(musicSoundId)
        musicSoundPool.release()

        effectsSoundPool.unload(shootSoundId)
        effectsSoundPool.unload(crashSoundId)
        effectsSoundPool.unload(shieldPopSoundId)
        effectsSoundPool.unload(explodeSoundId)
        effectsSoundPool.unload(forceWaveSoundId)
        effectsSoundPool.release()
    }
}