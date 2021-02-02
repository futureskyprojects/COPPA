package me.vistark.coppa.ui.auth.app_languages

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.domain.entity.Country
import me.vistark.fastdroid.R
import me.vistark.fastdroid.interfaces.IClickable
import me.vistark.fastdroid.utils.ViewExtension.onTap

class LAdapter(
    override var onClick: ((Country) -> Unit)?
) :
    RecyclerView.Adapter<LViewHolder>(), IClickable<Country> {

    val countries =
        RuntimeStorage.Countries.filter { it.cultureName != RuntimeStorage.SavedCulture || it.cultureName != RuntimeStorage.Translates.localization.currentCulture.cultureName }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flag_icon_with_name, parent, false)
        return LViewHolder(v)
    }

    override fun onBindViewHolder(holder: LViewHolder, position: Int) {
        val images = countries[position]
        holder.bind(images)
        holder.rvLanguageRoot.onTap {
            onClick?.invoke(images)
        }
    }

    override fun getItemCount(): Int {
        return countries.size
    }
}