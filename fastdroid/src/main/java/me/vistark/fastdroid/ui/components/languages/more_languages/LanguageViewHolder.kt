package me.vistark.fastdroid.ui.components.languages.more_languages

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import me.vistark.fastdroid.R
import me.vistark.fastdroid.utils.GlideUtils.load

class LanguageViewHolder(v: View, private val isCache: Boolean = false) :
    RecyclerView.ViewHolder(v) {
    val flagIcon: ImageView = v.findViewById(R.id.flagIcon)

    fun bind(image: String) {
        flagIcon.load(image, isCache)
    }

    fun bind(resId: Int) {
        flagIcon.load(resId)
    }
}