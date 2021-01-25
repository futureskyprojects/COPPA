package me.vistark.coppa.ui.species_sync_review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.vistark.coppa.R
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.domain.entity.Species
import me.vistark.coppa.domain.entity.SpeciesSync
import me.vistark.fastdroid.interfaces.IClickable
import me.vistark.fastdroid.utils.ViewExtension.onTap

class SpeciesSyncReviewAdapter() : RecyclerView.Adapter<SpeciesSyncReviewViewHolder>(),
    IClickable<SpeciesSync> {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeciesSyncReviewViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grid_options_item, parent, false)
        return SpeciesSyncReviewViewHolder(v)
    }

    override fun onBindViewHolder(holderSyncReview: SpeciesSyncReviewViewHolder, position: Int) {
        val current = RuntimeStorage.CurrentTripSync!!.hauls[position]
        holderSyncReview.bind(current)
        holderSyncReview.isciCvRoot.onTap {
            onClick?.invoke(current)
        }
    }

    override fun getItemCount(): Int {
        return RuntimeStorage.CurrentTripSync!!.hauls.size
    }

    override var onClick: ((SpeciesSync) -> Unit)? = null
}