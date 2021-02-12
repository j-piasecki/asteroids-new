package io.github.breskin.asteroids

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.github.breskin.asteroids.home.PowerUpsAdapter

class BottomSheetManager(private val context: Context, private val enterFullscreenCallback: () -> Unit) {

    fun openInfoDialog() {
        val dialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_info_layout, null)

        dialog.setOnCancelListener { enterFullscreenCallback() }
        dialog.setOnDismissListener { enterFullscreenCallback() }

        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    fun openPowerUpsDialog() {
        val dialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_power_ups_layout, null)

        val adapter = PowerUpsAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.fragment_power_ups_recycler_view)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        dialog.setOnCancelListener { enterFullscreenCallback() }
        dialog.setOnDismissListener { enterFullscreenCallback() }

        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }
}