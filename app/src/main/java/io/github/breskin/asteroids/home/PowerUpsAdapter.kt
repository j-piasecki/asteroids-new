package io.github.breskin.asteroids.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.github.breskin.asteroids.R
import io.github.breskin.asteroids.game.PowerState

class PowerUpsAdapter : RecyclerView.Adapter<PowerUpsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_power_up, null)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return PowerState.Power.AMOUNT
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(powerUp: Int) {
            val imageView = view.findViewById<ImageView>(R.id.row_power_up_icon)
            val textView = view.findViewById<TextView>(R.id.row_power_up_text)
            val power = PowerState.Power.get(powerUp)

            imageView.setImageBitmap(power.getBitmap())
            imageView.backgroundTintList = ColorStateList(arrayOf(intArrayOf()), intArrayOf(power.getColor()))

            textView.text = power.getDescription()
        }
    }
}