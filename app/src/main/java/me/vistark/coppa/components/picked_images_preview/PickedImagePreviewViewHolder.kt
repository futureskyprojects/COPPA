package me.vistark.coppa.components.picked_images_preview

import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import me.vistark.coppa.R

class PickedImagePreviewViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val iisoIvPreview: ImageView = v.findViewById(R.id.iisoIvPreview)
    val iisoCvRoot: CardView = v.findViewById(R.id.iisoCvRoot)
    val iisoCvDelete: CardView = v.findViewById(R.id.iisoCvDelete)

    fun bind(uri: Uri) {
        iisoIvPreview.setImageURI(uri)
    }
}