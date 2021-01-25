package me.vistark.coppa.ui.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.vistark.coppa.R
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.domain.entity.SpeciesCategory
import me.vistark.fastdroid.interfaces.IClickable
import me.vistark.fastdroid.utils.ViewExtension.onTap

class CategoryAdapter : RecyclerView.Adapter<CategoryViewHolder>(), IClickable<SpeciesCategory> {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grid_options_item, parent, false)
        return CategoryViewHolder(v)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val current = RuntimeStorage.SpeciesCategories[position]
        holder.bind(current)
        holder.isciCvRoot.onTap {
            onClick?.invoke(current)
        }
    }

    override fun getItemCount(): Int {
        return RuntimeStorage.SpeciesCategories.size
    }

    override var onClick: ((SpeciesCategory) -> Unit)? = null
}