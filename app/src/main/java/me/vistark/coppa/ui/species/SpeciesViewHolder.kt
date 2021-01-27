package me.vistark.coppa.ui.species

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import me.vistark.coppa.R
import me.vistark.coppa._core.utils.CorrectURL.coppaCorrectResourcePath
import me.vistark.coppa.domain.entity.Species
import me.vistark.fastdroid.utils.GlideUtils.load

class SpeciesViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val isciCvRoot: CardView = v.findViewById(R.id.isciCvRoot)
    val isciIvSpeciesCategoryImage: ImageView = v.findViewById(R.id.isciIvSpeciesCategoryImage)
    val isciTvSpeciesCategoryName: TextView = v.findViewById(R.id.isciTvSpeciesCategoryName)

    fun bind(species: Species) {
        isciIvSpeciesCategoryImage.load(species.image.coppaCorrectResourcePath(), true)
        isciTvSpeciesCategoryName.text = species.name
        isciTvSpeciesCategoryName.isSelected = true
    }
}