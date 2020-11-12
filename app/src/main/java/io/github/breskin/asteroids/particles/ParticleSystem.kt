package io.github.breskin.asteroids.particles

import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import android.os.Build
import io.github.breskin.asteroids.GameView
import io.github.breskin.asteroids.game.GameLogic
import java.lang.NullPointerException
import kotlin.random.Random

class ParticleSystem {

    private val particles = mutableListOf<Particle>()
    private val newParticles = mutableListOf<Particle>()

    private val maximumAllowed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) 150 else 30

    fun draw(canvas: Canvas, logic: GameLogic) {
        for (particle in particles)
            particle.draw(canvas, logic)
    }

    fun update(logic: GameLogic) {
        particles.addAll(newParticles)
        newParticles.clear()

        val purge = particles.size > maximumAllowed || GameView.frameTime > 20
        var counter = 0
        var i = 0

        try {
            while (i < particles.size) {
                counter++

                if (particles[i].toDelete || (purge && counter % 6 == 0)) {
                    particles.removeAt(i)
                    continue
                }

                particles[i].update(logic)
                i++
            }
        } catch (e: NullPointerException) {}
    }

    fun createInPoint(position: PointF, size: Float, count: Int, r: Int, g: Int, b: Int) {
        for (i in 0..count)
            newParticles.add(Particle(PointF(position.x, position.y), size, r, g, b))
    }

    fun createInArea(area: RectF, size: Float, count: Int, r: Int, g: Int, b: Int) {
        for (i in 0..count)
            newParticles.add(Particle(PointF(area.left + area.width() * Random.nextFloat(), area.top + area.height() * Random.nextFloat()), size, r, g, b))
    }
}