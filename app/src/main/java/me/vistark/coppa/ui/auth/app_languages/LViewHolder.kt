package me.vistark.coppa.ui.auth.app_languages

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import me.vistark.coppa._core.utils.CorrectURL.coppaCorrectResourcePath
import me.vistark.coppa.domain.entity.Country
import me.vistark.fastdroid.R
import me.vistark.fastdroid.utils.GlideUtils.load

class LViewHolder(v: View) :
    RecyclerView.ViewHolder(v) {
    val rvLanguageRoot: CardView = v.findViewById(R.id.rvLanguageRoot)
    val flagIcon: ImageView = v.findViewById(R.id.flagIcon)
    val flagName: TextView = v.findViewById(R.id.flagName)

    fun bind(country: Country) {
        flagIcon.load(country.flagIcon.coppaCorrectResourcePath(), true)
        flagName.text = country.displayName + " (${country.cultureName})"
    }
}