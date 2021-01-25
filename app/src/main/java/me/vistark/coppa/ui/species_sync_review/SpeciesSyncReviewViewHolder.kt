package me.vistark.coppa.ui.species_sync_review

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import me.vistark.coppa.R
import me.vistark.coppa._core.utils.CorrectURL.correctPath
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.domain.entity.Species
import me.vistark.coppa.domain.entity.SpeciesCategory
import me.vistark.coppa.domain.entity.SpeciesSync
import me.vistark.fastdroid.utils.AnimationUtils.fadeIn
import me.vistark.fastdroid.utils.AnimationUtils.fadeOut
import me.vistark.fastdroid.utils.GlideUtils.load
import me.vistark.fastdroid.utils.GlideUtils.path
import me.vistark.fastdroid.utils.MultipleLanguage.L
import java.util.*

class SpeciesSyncReviewViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val isciCvRoot: CardView = v.findViewById(R.id.isciCvRoot)
    val isciIvSpeciesCategoryImage: ImageView = v.findViewById(R.id.isciIvSpeciesCategoryImage)
    val isciTvSpeciesCategoryName: TextView = v.findViewById(R.id.isciTvSpeciesCategoryName)

    fun bind(speciesSync: SpeciesSync) {
        val images = speciesSync.images.split(",")
        var count = 0
        Timer().schedule(object : TimerTask() {
            override fun run() {
                isciIvSpeciesCategoryImage.post {
                    isciIvSpeciesCategoryImage.fadeOut(300) {
                        isciIvSpeciesCategoryImage.path(images[count++ % images.size])
                        isciIvSpeciesCategoryImage.fadeIn(300)
                    }
                }
            }

        }, 100, 3000)
        isciTvSpeciesCategoryName.text =
            RuntimeStorage.Specieses.firstOrNull { it.id == speciesSync.specieId }?.name ?: L(
                isciTvSpeciesCategoryName.context.getString(R.string.NotFound)
            )
        isciTvSpeciesCategoryName.isSelected = true
    }
}