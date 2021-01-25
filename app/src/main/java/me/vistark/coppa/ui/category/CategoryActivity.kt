package me.vistark.coppa.ui.category

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.activity_category.rlContainer
import kotlinx.android.synthetic.main.activity_category.rlRoot
import kotlinx.android.synthetic.main.activity_species.*
import me.vistark.coppa.R
import me.vistark.coppa.domain.entity.SpeciesCategory
import me.vistark.coppa.ui.species.SpeciesActivity
import me.vistark.fastdroid.ui.activities.FastdroidActivity
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownCenter
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpCenter
import me.vistark.fastdroid.utils.ViewExtension.onTap

class CategoryActivity :
    FastdroidActivity(
        R.layout.activity_category,
        isCanAutoTranslate = true
    ) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initSpecicesCategories()

        initEvents()

        rlContainer.scaleUpCenter()
    }

    private fun initEvents() {
        rlRoot.setOnClickListener {
            animateFinish()
        }
        acBtnClose.onTap {
            animateFinish()
        }
    }

    private fun initSpecicesCategories() {
        acRvSpeciesCategories.setHasFixedSize(true)
        acRvSpeciesCategories.layoutManager = GridLayoutManager(this, 2)

        val adapter = CategoryAdapter()
        adapter.onClick = {
            // Sự kiện khi nhấn chọn nhóm loài
            finish()
            // Khởi động màn hình danh sách loài và truyền mã nhóm loài
            val intent = Intent(this, SpeciesActivity::class.java)
            intent.putExtra(SpeciesCategory::class.java.simpleName, it.id)
            intent.putExtra(SpeciesActivity::class.java.simpleName, it.title)
            startActivity(intent)
            overridePendingTransition(-1, -1)
        }
        acRvSpeciesCategories.adapter = adapter

    }

    fun animateFinish() {
        rlContainer.scaleDownCenter {
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}