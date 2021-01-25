package me.vistark.coppa.ui.species

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_species.*
import kotlinx.android.synthetic.main.activity_species.rlContainer
import kotlinx.android.synthetic.main.activity_species.rlRoot
import me.vistark.coppa.R
import me.vistark.coppa.domain.entity.Species
import me.vistark.coppa.domain.entity.SpeciesCategory
import me.vistark.coppa.ui.category.CategoryActivity
import me.vistark.coppa.ui.specices_info_provider.SpeciesInfoProviderActivity
import me.vistark.fastdroid.ui.activities.FastdroidActivity
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownCenter
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpCenter
import me.vistark.fastdroid.utils.MultipleLanguage.L
import me.vistark.fastdroid.utils.ViewExtension.onTap

class SpeciesActivity :
    FastdroidActivity(
        R.layout.activity_species,
        isCanAutoTranslate = true
    ) {

    var selectedSpeciesCategoryId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Nếu thu thập dữ liệu đã có không thành công thì tiến hành kết thúc màn hình hiện tại
        if (!collectReceiveDatas())
            return

        initSpecicesCategories()

        initEvents()

        rlContainer.scaleUpCenter()
    }


    private fun collectReceiveDatas(): Boolean {
        selectedSpeciesCategoryId = intent.getIntExtra(SpeciesCategory::class.java.simpleName, -1)
        val label = intent.getStringExtra(SpeciesActivity::class.java.simpleName) ?: ""
        if (label.isNotEmpty())
            asTvTitle.text = label

        if (selectedSpeciesCategoryId <= 0) {
            Toasty.error(
                this,
                L(getString(R.string.CanNotGetSpeciesCategory)),
                Toasty.LENGTH_SHORT,
                true
            ).show()
            onBackPressed()
            return false
        }
        return true
    }

    private fun initEvents() {
        rlRoot.setOnClickListener {
            animateFinish()
        }
        asBtnClose.onTap {
            animateFinish()
        }
        asIvBackIcon.onTap {
            onBackPressed()
        }
    }

    private fun initSpecicesCategories() {
        asRvSpecies.setHasFixedSize(true)
        asRvSpecies.layoutManager = GridLayoutManager(this, 2)

        val adapter = SpeciesAdapter(selectedSpeciesCategoryId)
        adapter.onClick = {
            // Sự kiện khi nhấn chọn nhóm loài, khởi động trang cung cấp thông tin
            val intent = Intent(this, SpeciesInfoProviderActivity::class.java)
            intent.putExtra(Species::class.java.simpleName, it.id)
            intent.putExtra(SpeciesInfoProviderActivity::class.java.simpleName, it.name)
            startActivity(intent)
            overridePendingTransition(-1, -1)
        }
        asRvSpecies.adapter = adapter
    }

    override fun onBackPressed() {
        rlContainer.scaleDownCenter {
            startActivity(CategoryActivity::class.java)
            finish()
        }
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