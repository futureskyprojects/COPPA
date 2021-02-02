package me.vistark.coppa.ui.trip_hostory

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_trip_history.*
import kotlinx.android.synthetic.main.layout_home_header_component.*
import me.vistark.coppa.R
import me.vistark.coppa.services.BackgroundService
import me.vistark.coppa.ui.category.CategoryActivity
import me.vistark.coppa.ui.home.HomeActivity
import me.vistark.fastdroid.core.models.FastdroidCoordinate
import me.vistark.fastdroid.ui.activities.FastdroidActivity
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownCenter
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpCenter
import me.vistark.fastdroid.utils.ViewExtension.onTap
import me.vistark.fastdroid.utils.locations.LocationUtils

class TripHistoryActivity : FastdroidActivity(
    R.layout.activity_trip_history,
    isCanAutoTranslate = true
) {
    val adapter = TripHistoryAdapter()

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

        adapter.onClick = {
        }
        asRvSpecies.adapter = adapter
    }

    override fun onSignal(key: String, value: String) {
        if (key == BackgroundService.SYNC_DONE) {
            adapter.sync {
                runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
            }
        }
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