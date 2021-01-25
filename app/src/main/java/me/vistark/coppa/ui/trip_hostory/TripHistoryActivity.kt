package me.vistark.coppa.ui.trip_hostory

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_trip_history.*
import me.vistark.coppa.R
import me.vistark.coppa.ui.category.CategoryActivity
import me.vistark.fastdroid.ui.activities.FastdroidActivity
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownCenter
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpCenter
import me.vistark.fastdroid.utils.ViewExtension.onTap

class TripHistoryActivity : FastdroidActivity(
    R.layout.activity_trip_history,
    isCanAutoTranslate = true
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initDatas()

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

    private fun initDatas() {
        asRvSpecies.setHasFixedSize(true)
        asRvSpecies.layoutManager = LinearLayoutManager(this)

        val adapter = TripHistoryAdapter()
        adapter.onClick = {
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