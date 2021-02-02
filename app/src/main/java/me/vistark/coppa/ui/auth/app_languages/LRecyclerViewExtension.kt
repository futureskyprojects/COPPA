package me.vistark.coppa.ui.auth.app_languages

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.vistark.coppa.domain.entity.Country

object LRecyclerViewExtension {
    fun RecyclerView.bindL(
        onClicked: ((Country) -> Unit)?
    ) {
        this.setHasFixedSize(true)
        val lm = LinearLayoutManager(context)
        lm.orientation = LinearLayoutManager.VERTICAL
        this.layoutManager = lm

        val adapter = LAdapter(onClicked)
        this.adapter = adapter
    }
}