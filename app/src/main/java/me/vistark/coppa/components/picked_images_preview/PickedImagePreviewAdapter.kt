package me.vistark.coppa.components.picked_images_preview

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.vistark.coppa.R
import me.vistark.fastdroid.interfaces.IClickable
import me.vistark.fastdroid.interfaces.IDeletable
import me.vistark.fastdroid.interfaces.ILongClickable
import me.vistark.fastdroid.utils.ViewExtension.onTap

class PickedImagePreviewAdapter(val imageUris: ArrayList<Uri>) :
    RecyclerView.Adapter<PickedImagePreviewViewHolder>(), IClickable<Uri>, ILongClickable<Uri>,
    IDeletable<Int> {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PickedImagePreviewViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_square_only, parent, false)
        return PickedImagePreviewViewHolder(v)
    }

    override fun onBindViewHolder(holder: PickedImagePreviewViewHolder, position: Int) {
        val crr = imageUris[position]
        holder.bind(crr)
        holder.iisoCvRoot.onTap { onClick?.invoke(crr) }
        holder.iisoCvRoot.setOnLongClickListener {
            onLongClick?.invoke(crr)
            return@setOnLongClickListener true
        }
        holder.iisoCvDelete.onTap { onDelete?.invoke(position) }
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    override var onClick: ((Uri) -> Unit)? = null

    override var onLongClick: ((Uri) -> Unit)? = null
    override var onDelete: ((Int) -> Unit)? = null
}