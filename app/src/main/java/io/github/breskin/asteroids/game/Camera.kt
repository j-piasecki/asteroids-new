package io.github.breskin.asteroids.game

import android.graphics.PointF
import android.util.Log
import io.github.breskin.asteroids.GameView

class Camera {

    fun translatePosition(logic: GameLogic, position: PointF): PointF {
        val playerDistanceY = (logic.space.height * 0.5f + logic.player.position.y) / logic.space.height
        val playerDistanceX = (logic.space.width * 0.5f + logic.player.position.x) / logic.space.width

        val distanceY = (logic.space.height * 0.5f + position.y) / logic.space.height
        val distanceX = (logic.space.width * 0.5f + position.x) / logic.space.width

        val diffX = distanceX - playerDistanceX
        val diffY = distanceY - playerDistanceY

        return PointF(
                GameView.viewWidth * 0.5f + GameView.viewWidth * (playerDistanceX - 0.5f) + diffX * logic.space.width,
                GameView.viewHeight * 0.5f + GameView.viewHeight * (playerDistanceY - 0.5f) + diffY * logic.space.height
        )
    }

    fun getShipPosition(logic: GameLogic): PointF {
        val distanceY = logic.space.height * 0.5f + logic.player.position.y
        val distanceX = logic.space.width * 0.5f + logic.player.position.x

        return PointF(
            GameView.viewWidth * 0.5f + GameView.viewWidth * (distanceX / logic.space.width - 0.5f),
            GameView.viewHeight * 0.5f + GameView.viewHeight * (distanceY / logic.space.height - 0.5f)
        )
    }
}