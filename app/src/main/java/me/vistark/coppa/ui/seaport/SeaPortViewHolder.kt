package me.vistark.coppa.ui.seaport

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import me.vistark.coppa.R
import me.vistark.coppa.domain.entity.SeaPort

class SeaPortViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val iltoiCvRoot: CardView = v.findViewById(R.id.iltoiCvRoot)
    val iltoiTvContent: TextView = v.findViewById(R.id.iltoiTvContent)

    fun bind(seaPort: SeaPort) {
        iltoiTvContent.text = "${seaPort.name} (${seaPort.symbol})"
        iltoiTvContent.isSelected = true
    }
}