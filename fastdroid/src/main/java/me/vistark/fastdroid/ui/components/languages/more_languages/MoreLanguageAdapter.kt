package me.vistark.fastdroid.ui.components.languages.more_languages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.vistark.fastdroid.R
import me.vistark.fastdroid.interfaces.IClickable
import me.vistark.fastdroid.utils.ViewExtension.onTap

class MoreLanguageAdapter(val flags: ArrayList<String>, override var onClick: ((String) -> Unit)?) :
    RecyclerView.Adapter<MoreLanguageViewHolder>(), IClickable<String> {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreLanguageViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_flag_icon, parent, false)
        return MoreLanguageViewHolder(v)
    }

    override fun onBindViewHolder(holder: MoreLanguageViewHolder, position: Int) {
        val images = flags[position]
        holder.bind(images)
        holder.flagIcon.onTap {
            onClick?.invoke(images)
        }
    }

    override fun getItemCount(): Int {
        return flags.size
    }
}