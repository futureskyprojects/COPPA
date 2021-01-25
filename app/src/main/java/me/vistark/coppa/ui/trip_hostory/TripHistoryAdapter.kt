package me.vistark.coppa.ui.trip_hostory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.vistark.coppa.R
import me.vistark.coppa._core.api.APIService
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.domain.entity.Species
import me.vistark.coppa.domain.entity.TripLog
import me.vistark.fastdroid.interfaces.IClickable
import me.vistark.fastdroid.utils.ViewExtension.onTap
import retrofit2.await

class TripHistoryAdapter : RecyclerView.Adapter<TripHistoryViewHolder>(),
    IClickable<TripLog> {

    val runtimes = ArrayList<TripLog>()

    init {
        runtimes.addAll(RuntimeStorage.TripLogs)
        RuntimeStorage.TripSyncs.forEach {
            runtimes.add(0, it.toTripLog())
        }

        GlobalScope.launch {
            try {
                // Lấy danh sách lịch sử chuyến đi biển
                val tripLogs = APIService().APIs.getTripHistories().await()
                tripLogs.result?.apply {
                    RuntimeStorage.TripLogs = this.toTypedArray()
                    runtimes.clear()
                    runtimes.addAll(this.toTypedArray())
                    RuntimeStorage.TripSyncs.forEach {
                        runtimes.add(0, it.toTripLog())
                    }
                    notifyDataSetChanged()
                }
            } catch (e: Exception) {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripHistoryViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trip_history, parent, false)
        return TripHistoryViewHolder(v)
    }

    override fun onBindViewHolder(holder: TripHistoryViewHolder, position: Int) {
        val current = runtimes[position]
        holder.bind(current)
        holder.iithRoot.onTap {
            onClick?.invoke(current)
        }
    }

    override fun getItemCount(): Int {
        return runtimes.size
    }

    override var onClick: ((TripLog) -> Unit)? = null
}