package me.vistark.coppa.ui.species

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.vistark.coppa.R
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.domain.entity.Species
import me.vistark.fastdroid.interfaces.IClickable
import me.vistark.fastdroid.utils.ViewExtension.onTap

class SpeciesAdapter(speciesCategoryId: Int) : RecyclerView.Adapter<SpeciesViewHolder>(),
    IClickable<Species> {

    val species = RuntimeStorage.Specieses.filter { it.familyId == speciesCategoryId }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeciesViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grid_options_item, parent, false)
        return SpeciesViewHolder(v)
    }

    override fun onBindViewHolder(holder: SpeciesViewHolder, position: Int) {
        val current = species[position]
        holder.bind(current)
        holder.isciCvRoot.onTap {
            onClick?.invoke(current)
        }
    }

    override fun getItemCount(): Int {
        return species.size
    }

    override var onClick: ((Species) -> Unit)? = null
}