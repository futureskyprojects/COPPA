package me.vistark.fastdroid.ui.components.languages.more_languages

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

object MoreLangueExtensionForRecyclerView {
    fun RecyclerView.bindMoreLanguage(images: ArrayList<String>, onClicked: ((String) -> Unit)?) {
        this.setHasFixedSize(true)
        val lm = LinearLayoutManager(context)
        lm.orientation = LinearLayoutManager.HORIZONTAL
        this.layoutManager = lm

        val adapter = MoreLanguageAdapter(images, onClicked)
        this.adapter = adapter
    }
}