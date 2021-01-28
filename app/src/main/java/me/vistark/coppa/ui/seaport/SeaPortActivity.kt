package me.vistark.coppa.ui.seaport

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_species.*
import me.vistark.coppa.R
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.domain.entity.SeaPort
import me.vistark.coppa.ui.category.CategoryActivity
import me.vistark.coppa.ui.species.SpeciesAdapter
import me.vistark.fastdroid.ui.activities.FastdroidActivity
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownCenter
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpCenter
import me.vistark.fastdroid.utils.MultipleLanguage.L
import me.vistark.fastdroid.utils.ViewExtension.onTap

class SeaPortActivity : FastdroidActivity(
    R.layout.activity_sea_port,
    isCanAutoTranslate = true
) {
    companion object {
        val PICK_SEA_PORT_REQUEST_CODE = 8347

        fun AppCompatActivity.StartPickSeaPort(isSlectForStartSeaPort: Boolean) {
            val intent = Intent(this, SeaPortActivity::class.java)
            intent.putExtra(SeaPortActivity::class.java.simpleName, isSlectForStartSeaPort)
            startActivityForResult(intent, PICK_SEA_PORT_REQUEST_CODE)
        }

        fun onPickSeaPortResultProcessing(
            requestCode: Int,
            resultCode: Int,
            data: Intent?,
            onResult: (SeaPort, Boolean) -> Unit
        ) {
            if (requestCode == PICK_SEA_PORT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                val id = data.getIntExtra(SeaPort::class.java.simpleName, -1)
                val isSlectForStartSeaPort =
                    data.getBooleanExtra(SeaPortActivity::class.java.simpleName, false)
                val sp = RuntimeStorage.SeaPorts.firstOrNull { it.id == id }
                sp?.apply {
                    onResult.invoke(this, isSlectForStartSeaPort)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    var isSlectForStartSeaPort: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Nếu thu thập dữ liệu đã có không thành công thì tiến hành kết thúc màn hình hiện tại
        if (!collectReceiveDatas())
            return

        initDatas()

        initEvents()

        rlContainer.scaleUpCenter()

        // Mặc định là kết quả đóng
        setResult(RESULT_CANCELED)
    }


    private fun collectReceiveDatas(): Boolean {
        isSlectForStartSeaPort =
            intent.getBooleanExtra(SeaPortActivity::class.java.simpleName, false)

        if (isSlectForStartSeaPort) {
            asTvTitle.text = L(getString(R.string.PortOfDeparture))
        } else {
            asTvTitle.text = L(getString(R.string.PortOfReturn))
        }
        asTvTitle.isSelected = true
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

    private fun initDatas() {
        asRvSpecies.setHasFixedSize(true)
        asRvSpecies.layoutManager = LinearLayoutManager(this)

        val adapter = SeaPortAdapter()
        adapter.onClick = {
            val intent = Intent()
            intent.putExtra(SeaPort::class.java.simpleName, it.id)
            intent.putExtra(SeaPortActivity::class.java.simpleName, isSlectForStartSeaPort)
            setResult(RESULT_OK, intent)
            // Sự kiện khi nhấn chọn nhóm loài
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