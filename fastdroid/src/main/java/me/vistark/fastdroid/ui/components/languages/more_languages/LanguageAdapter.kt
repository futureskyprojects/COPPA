package me.vistark.fastdroid.ui.components.languages.more_languages

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.vistark.fastdroid.R
import me.vistark.fastdroid.interfaces.IClickable
import me.vistark.fastdroid.utils.ViewExtension.onTap

class LanguageAdapter(
    val flags: Array<String>,
    val currentImages: String = "",
    private val isCache: Boolean = false,
    override var onClick: ((String) -> Unit)?
) :
    RecyclerView.Adapter<LanguageViewHolder>(), IClickable<String> {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_flag_icon, parent, false)
        return LanguageViewHolder(v, isCache)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val images = flags[position]
        holder.bind(images)
        Log.e("COMPARE", "in[$currentImages] <> now[$images]")
        if (currentImages == images)
            holder.flagIcon.alpha = 0.3F
        else {
            holder.flagIcon.onTap {
                onClick?.invoke(images)
            }
        }
    }

    override fun getItemCount(): Int {
        return flags.size
    }
}