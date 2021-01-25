package me.vistark.coppa.ui.seaport

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.vistark.coppa.R
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.domain.entity.SeaPort
import me.vistark.coppa.domain.entity.Species
import me.vistark.fastdroid.interfaces.IClickable
import me.vistark.fastdroid.utils.ViewExtension.onTap

class SeaPortAdapter() : RecyclerView.Adapter<SeaPortViewHolder>(),
    IClickable<SeaPort> {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeaPortViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_line_text_options_item, parent, false)
        return SeaPortViewHolder(v)
    }

    override fun onBindViewHolder(holder: SeaPortViewHolder, position: Int) {
        val current = RuntimeStorage.SeaPorts[position]
        holder.bind(current)
        holder.iltoiCvRoot.onTap {
            onClick?.invoke(current)
        }
    }

    override fun getItemCount(): Int {
        return RuntimeStorage.SeaPorts.size
    }

    override var onClick: ((SeaPort) -> Unit)? = null
}