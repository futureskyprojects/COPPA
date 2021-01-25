package me.vistark.coppa.ui.trip_hostory

import android.annotation.SuppressLint
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import me.vistark.coppa.R
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.domain.entity.TripLog
import me.vistark.fastdroid.utils.AnimationUtils.fadeIn
import me.vistark.fastdroid.utils.MultipleLanguage.L

class TripHistoryViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val iithRoot: CardView = v.findViewById(R.id.iithRoot)

    val ithLnIsSyncing: LinearLayout = v.findViewById(R.id.ithLnIsSyncing)

    val ithTvStartSeaPortName: TextView = v.findViewById(R.id.ithTvStartSeaPortName)
    val ithTvStartSeaPortSymbol: TextView = v.findViewById(R.id.ithTvStartSeaPortSymbol)
    val ithTvStartDepartureTime: TextView = v.findViewById(R.id.ithTvStartDepartureTime)

    val ithTvEndSeaPortName: TextView = v.findViewById(R.id.ithTvEndSeaPortName)
    val ithTvEndSeaPortSymbol: TextView = v.findViewById(R.id.ithTvEndSeaPortSymbol)
    val ithTvEndDepartureTime: TextView = v.findViewById(R.id.ithTvEndDepartureTime)


    @SuppressLint("SetTextI18n")
    fun bind(tripLog: TripLog) {
        if (tripLog.id <= 0) {
            ithLnIsSyncing.visibility = View.VISIBLE
        }
        val startSeaPort = RuntimeStorage.SeaPorts.firstOrNull { it.id == tripLog.departurePort }
        val endSeaPort = RuntimeStorage.SeaPorts.firstOrNull { it.id == tripLog.destinationPort }

        ithTvStartSeaPortName.apply {
            text = startSeaPort?.name ?: L(iithRoot.context.getString(R.string.NotFound))
            isSelected = true
        }
        ithTvStartSeaPortSymbol.apply {
            text = "(${startSeaPort?.symbol ?: L(iithRoot.context.getString(R.string.NotFound))})"
            isSelected = true
        }
        ithTvStartDepartureTime.apply {
            text = tripLog.departureTime
            isSelected = true
        }

        ithTvEndSeaPortName.apply {
            text = endSeaPort?.name ?: L(iithRoot.context.getString(R.string.NotFound))
            isSelected = true
        }
        ithTvEndSeaPortSymbol.apply {
            text = "(${endSeaPort?.symbol ?: L(iithRoot.context.getString(R.string.NotFound))})"
            isSelected = true
        }
        ithTvEndDepartureTime.apply {
            text = tripLog.departureTime
            isSelected = true
        }
    }
}