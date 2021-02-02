package me.vistark.coppa.ui.species_sync_review

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import me.vistark.coppa.R
import me.vistark.coppa._core.utils.CorrectURL.coppaCorrectResourcePath
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.domain.entity.SpeciesSync
import me.vistark.fastdroid.utils.AnimationUtils.fadeIn
import me.vistark.fastdroid.utils.AnimationUtils.fadeOut
import me.vistark.fastdroid.utils.GlideUtils.load
import me.vistark.fastdroid.utils.GlideUtils.path
import me.vistark.fastdroid.utils.MultipleLanguage.L
import me.vistark.fastdroid.utils.storage.FastdroidFileUtils.correctPath
import java.util.*

class SpeciesSyncReviewViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val isciCvRoot: CardView = v.findViewById(R.id.isciCvRoot)
    val isciIvSpeciesCategoryImage: ImageView = v.findViewById(R.id.isciIvSpeciesCategoryImage)
    val isciTvSpeciesCategoryName: TextView = v.findViewById(R.id.isciTvSpeciesCategoryName)

    fun bind(speciesSync: SpeciesSync) {
        val images = speciesSync.images.split(",").filter { it.isNotEmpty() && it.isNotBlank() }
        var count = 0

        // Nếu số lượng hình ảnh lớn hơn 1 thì tiến hành cho chạy slide
        if (images.size > 1) {
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
        } else if (images.isNotEmpty()) {
            // Nếu số lượng hình ảnh là 1 thì hiển thị ảnh và không chạy slide
            isciIvSpeciesCategoryImage.path(images.first())
            isciIvSpeciesCategoryImage.fadeIn(300)
        } else {
            // Nếu không tồn tại hình ảnh thì tiến hành cho hiển thị hình ảnh mặc định
            val spieciesImage =
                RuntimeStorage.Specieses.firstOrNull { it.id == speciesSync.specieId }?.image ?: ""
            isciIvSpeciesCategoryImage.load(spieciesImage.coppaCorrectResourcePath())
            isciIvSpeciesCategoryImage.fadeIn(300)
        }
        isciTvSpeciesCategoryName.text =
            RuntimeStorage.Specieses.firstOrNull { it.id == speciesSync.specieId }?.name ?: L(
                isciTvSpeciesCategoryName.context.getString(R.string.NotFound)
            )
        isciTvSpeciesCategoryName.isSelected = true
    }
}