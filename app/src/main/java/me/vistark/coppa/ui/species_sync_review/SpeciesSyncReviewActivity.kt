package me.vistark.coppa.ui.species_sync_review

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_species.*
import me.vistark.coppa.R
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.domain.entity.Species
import me.vistark.coppa.domain.entity.SpeciesCategory
import me.vistark.coppa.domain.entity.SpeciesSync
import me.vistark.coppa.ui.category.CategoryActivity
import me.vistark.coppa.ui.specices_info_provider.SpeciesInfoProviderActivity
import me.vistark.coppa.ui.species.SpeciesActivity
import me.vistark.coppa.ui.species.SpeciesAdapter
import me.vistark.fastdroid.ui.activities.FastdroidActivity
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownCenter
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpCenter
import me.vistark.fastdroid.utils.MultipleLanguage
import me.vistark.fastdroid.utils.MultipleLanguage.L
import me.vistark.fastdroid.utils.ViewExtension.onTap

class SpeciesSyncReviewActivity : FastdroidActivity(
    R.layout.activity_species_sync_review,
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

        val adapter = SpeciesSyncReviewAdapter()
        adapter.onClick = { species ->
            // Sự kiện khi nhấn chọn nhóm loài, khởi động trang cung cấp thông tin
            val intent = Intent(this, SpeciesInfoProviderActivity::class.java)
            intent.putExtra(Species::class.java.simpleName, species.specieId)
            intent.putExtra(
                SpeciesInfoProviderActivity::class.java.simpleName,
                RuntimeStorage.Specieses.firstOrNull { it.id == species.specieId }?.name ?: L(
                    getString(R.string.NotFound)
                )
            )
            intent.putExtra(SpeciesSync::class.java.simpleName, Gson().toJson(species))
            startActivity(intent)
            overridePendingTransition(-1, -1)
            animateFinish()
        }
        asRvSpecies.adapter = adapter
    }

    override fun onBackPressed() {
        rlContainer.scaleDownCenter {
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