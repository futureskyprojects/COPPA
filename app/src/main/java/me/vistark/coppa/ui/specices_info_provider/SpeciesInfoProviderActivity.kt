package me.vistark.coppa.ui.specices_info_provider

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_species.asBtnConfirm
import kotlinx.android.synthetic.main.activity_species.asIvBackIcon
import kotlinx.android.synthetic.main.activity_species.asTvTitle
import kotlinx.android.synthetic.main.activity_species.rlContainer
import kotlinx.android.synthetic.main.activity_species.rlRoot
import kotlinx.android.synthetic.main.activity_species_info_provider.*
import me.vistark.coppa.R
import me.vistark.coppa.application.DefaultValue.MinImageRequest
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.components.picked_images_preview.PickedImagePreviewAdapter
import me.vistark.coppa.domain.entity.Species
import me.vistark.coppa.domain.entity.SpeciesSync
import me.vistark.coppa.domain.entity.TripSync.Companion.addSpeciesSync
import me.vistark.coppa.ui.home.HomeActivity
import me.vistark.coppa.ui.species_sync_review.SpeciesSyncReviewActivity
import me.vistark.fastdroid.ui.activities.FastdroidActivity
import me.vistark.fastdroid.ui.overlay.LoadingBase.showLoadingBase
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownBottomLeft
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownCenter
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownTopRight
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpBottomLeft
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpCenter
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpTopRight
import me.vistark.fastdroid.utils.MultipleLanguage
import me.vistark.fastdroid.utils.MultipleLanguage.L
import me.vistark.fastdroid.utils.UriUtils.saveImage
import me.vistark.fastdroid.utils.ViewExtension.binDateTimePicker
import me.vistark.fastdroid.utils.ViewExtension.bindPopupMenu
import me.vistark.fastdroid.utils.ViewExtension.onTap
import me.vistark.fastdroid.utils.ViewExtension.onTextChanged
import me.vistark.fastdroid.utils.storage.FastdroidFileUtils.deleteOnExists
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SpeciesInfoProviderActivity :
    FastdroidActivity(R.layout.activity_species_info_provider, isCanAutoTranslate = true) {

    var currentSpeciesSync = SpeciesSync()

    val pickedImagesUris = ArrayList<Uri>()
    val mapImagesAddress = HashMap<String, Uri>()
    lateinit var adapter: PickedImagePreviewAdapter

    var selectedSpeciesId = -1

    var isUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Nếu thu thập dữ liệu đã có không thành công thì tiến hành kết thúc màn hình hiện tại
        if (!collectReceiveDatas())
            return

        // Khởi tạo hành động cho việc update nếu đang ở chế độ update
        if (isUpdate)
            initUpdateData()

        initEvents()

        pickedImages()

        rlContainer.scaleUpCenter()

        // Gọi validate lại một lần để khóa nút nếu form không hợp lệ
        validate()

    }

    private fun initUpdateData() {
        // Tìm cách xóa ảnh trong folder khi bị xóa qua cập nhật
        currentSpeciesSync.images.split(",").forEach {
            val newUri = it.toUri()
            mapImagesAddress.put(it, newUri)
            pickedImagesUris.add(newUri)
        }
        edtSpeciesLength.setText(currentSpeciesSync.length.toString())
        edtSpeciesWeight.setText(currentSpeciesSync.weight.toString())
        tvCatchedAt.setText(currentSpeciesSync.catchedAt)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.ipoCamera)?.title = L(getString(R.string.camera))
        menu?.findItem(R.id.ipoGallery)?.title = L(getString(R.string.gallery))
        return super.onPrepareOptionsMenu(menu)
    }

    private fun pickedImages() {
        rvPickedImages.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rvPickedImages.layoutManager = layoutManager

        adapter = PickedImagePreviewAdapter(pickedImagesUris)
        adapter.onDelete = {
            val crrUri = pickedImagesUris[it]
            pickedImagesUris.removeAt(it)

            // Nếu ảnh thuộc một ảnh trước đó, và giờ đang cần xóa
            mapImagesAddress.entries.forEach { map ->
                if (map.value == crrUri) {
                    mapImagesAddress.remove(map.key)
                    map.key.deleteOnExists()
                    return@forEach
                }
            }
            updatePickedImages()
        }
        rvPickedImages.adapter = adapter
    }

    fun updatePickedImages() {
        adapter.notifyDataSetChanged()
        if (pickedImagesUris.isEmpty()) {
            asipLnNoPickedImages.scaleUpTopRight()
            rvPickedImages.scaleDownBottomLeft()
        } else {
            asipLnNoPickedImages.scaleDownTopRight()
            rvPickedImages.scaleUpBottomLeft()
        }
        validate()
    }


    private fun collectReceiveDatas(): Boolean {
        selectedSpeciesId = intent.getIntExtra(Species::class.java.simpleName, -1)
        val label = intent.getStringExtra(SpeciesInfoProviderActivity::class.java.simpleName) ?: ""
        if (label.isNotEmpty())
            asTvTitle.text = label

        if (selectedSpeciesId <= 0) {
            Toasty.error(
                this,
                L(getString(R.string.CanNotGetSpeciesCategory)),
                Toasty.LENGTH_SHORT,
                true
            ).show()
            onBackPressed()
            return false
        }

        currentSpeciesSync.specieId = selectedSpeciesId

        val rawData = intent.getStringExtra(SpeciesSync::class.java.simpleName)
        if (!rawData.isNullOrEmpty()) {
            // Nếu raw data truyền sang không rỗng hay null, tức đang thực hiện hành động update
            currentSpeciesSync = Gson().fromJson(rawData, SpeciesSync::class.java)
            isUpdate = true
        }

        return true
    }

    private fun initEvents() {
        rlRoot.setOnClickListener {
            animateFinish()
        }

        asIvBackIcon.onTap {
            animateFinish()
        }
        asipLnNoPickedImages.setOnClickListener {
            asIvAddImage.performClick()
        }
        asIvAddImage.bindPopupMenu(R.menu.image_picker_options) {
            when (it) {
                R.id.ipoCamera -> {
                    takePhotoForUri { uri ->
                        uri?.apply {
                            pickedImagesUris.add(this)
                            updatePickedImages()
                        }
                    }
                    return@bindPopupMenu true
                }
                R.id.ipoGallery -> {
                    // Pick image/Take photo
                    pickImageForUri {
                        pickedImagesUris.add(it)
                        updatePickedImages()
                    }
                    return@bindPopupMenu true
                }
                else -> {
                    return@bindPopupMenu false
                }
            }
        }

        tvCatchedAt.binDateTimePicker {
            validate()
            currentSpeciesSync.catchedAt = tvCatchedAt.text.toString()
        }

        edtSpeciesLength.onTextChanged {
            validate()
            try {
                val temp: Float? = it.toFloatOrNull()
                if (temp != null && temp >= 0) {
                    currentSpeciesSync.length = temp
                    return@onTextChanged
                }
            } catch (e: Exception) {

            }
            edtSpeciesLength.error = L(getString(R.string.LengIsInvalid))
        }

        edtSpeciesWeight.onTextChanged {
            validate()
            try {
                val temp: Float? = it.toFloatOrNull()
                if (temp != null && temp >= 0) {
                    currentSpeciesSync.weight = temp
                    return@onTextChanged
                }
            } catch (e: Exception) {

            }
            edtSpeciesLength.error = L(getString(R.string.WeightIsInvalid))
        }


        asBtnConfirm.onTap {
            if (pickedImagesUris.isEmpty() || pickedImagesUris.size < MinImageRequest) {
                Toasty.error(
                    this,
                    String.format(
                        L(getString(R.string.YouMustPickAtleast_ImageAboutThisSpecies)),
                        MinImageRequest
                    ),
                    Toasty.LENGTH_SHORT,
                    true
                ).show()
                return@onTap
            }

            if (validate()) {
                if (!isUpdate) {
                    // Nếu không phải là trường hợp update thì tiến hành kiểm tra tọa độ
                    if (HomeActivity.PUBLIC_CURRENT_COORDINATES == null) {
                        Toasty.error(
                            this,
                            L(getString(R.string.PleaseWaitForAFewMinutesSystemIsDetectYourLocationCoordinates)),
                            Toasty.LENGTH_SHORT,
                            true
                        ).show()
                        return@onTap
                    } else {
                        // Nếu đã có vị trí
                        currentSpeciesSync.long =
                            HomeActivity.PUBLIC_CURRENT_COORDINATES!!.longitude.toString()
                        currentSpeciesSync.lat =
                            HomeActivity.PUBLIC_CURRENT_COORDINATES!!.latitude.toString()

                    }
                }
                // Tiến hành xử lý và lưu (hình ảnh & dữ liệu)
                saveProcessing()
            } else {
                Toasty.error(
                    this,
                    L(getString(R.string.YouMustCorrectAllFiled)),
                    Toasty.LENGTH_SHORT,
                    true
                ).show()
            }
        }
    }

    private fun saveProcessing() {
        // Hiển thị dialog loading
        val loading = showLoadingBase(L(getString(R.string.SavingYourCatchedSpecies)))
        val snapshotPickedImages = ArrayList<String>()

        // Tự động thêm cách ảnh đã có trước đó vào danh sách
        snapshotPickedImages.addAll(mapImagesAddress.keys)

        Thread {
            // Lưu danh sách các ảnh này vào cache
            pickedImagesUris.forEach { uri ->
                if (!mapImagesAddress.containsValue(uri)) {
                    // Nếu không có trong danh sách map, tiến hành lưu lại
                    uri.saveImage(
                        this,
                        filename = "/snapshot/data/trip_${RuntimeStorage.CurrentTripSync?.getDesTime()}/${UUID.randomUUID()}.jpg"
                    ).apply {
                        if (this.isNotEmpty()) {
                            snapshotPickedImages.add(this)
                        }
                    }
                }
            }

            // Cập nhật vào SpeciesSync
            currentSpeciesSync.images = snapshotPickedImages.joinToString(",")

            // Thêm mới vào chuyến đi hiện tại
            addSpeciesSync(currentSpeciesSync)

            runOnUiThread {
                loading.dismiss()
                Toasty.success(
                    this,
                    L(getString(R.string.SaveSpeciesSuccessful)),
                    Toasty.LENGTH_SHORT,
                    true
                ).show()
                animateFinish()
            }
        }.start()

    }

    fun validate(): Boolean {
        if (pickedImagesUris.size < MinImageRequest ||
            edtSpeciesLength.text.isEmpty() ||
            edtSpeciesLength.text.toString().toFloatOrNull() == null ||
            edtSpeciesWeight.text.isEmpty() ||
            edtSpeciesLength.text.toString().toFloatOrNull() == null ||
            tvCatchedAt.text.isEmpty()
        ) {
            asBtnConfirm.isEnabled = false
            return false
        } else {
            asBtnConfirm.isEnabled = true
            return true
        }
    }

    fun animateFinish() {
        rlContainer.scaleDownCenter {
            if (isUpdate) {
                // Khoải động màn hình danh sách các loài đã bắt
                startActivity(SpeciesSyncReviewActivity::class.java)
                overridePendingTransition(-1, -1)
            }
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}