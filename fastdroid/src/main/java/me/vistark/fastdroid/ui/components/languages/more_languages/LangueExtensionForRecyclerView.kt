package me.vistark.fastdroid.ui.components.languages.more_languages

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

object LangueExtensionForRecyclerView {
    fun RecyclerView.bindMoreLanguage(
        images: Array<String>,
        currentImages: String = "",
        isCache: Boolean = false,
        onClicked: ((String) -> Unit)?
    ) {
        this.setHasFixedSize(true)
        val lm = LinearLayoutManager(context)
        lm.orientation = LinearLayoutManager.HORIZONTAL
        this.layoutManager = lm

        val adapter = LanguageAdapter(images, currentImages, isCache, onClicked)
        this.adapter = adapter
    }
}