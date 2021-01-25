package me.vistark.coppa.ui.specices_info_provider

import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
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
import me.vistark.coppa.components.picked_images_preview.PickedImagePreviewAdapter
import me.vistark.coppa.domain.entity.Species
import me.vistark.coppa.domain.entity.SpeciesSync
import me.vistark.coppa.domain.entity.TripSync.Companion.addSpeciesSync
import me.vistark.coppa.ui.home.HomeActivity
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
import me.vistark.fastdroid.utils.ViewExtension.onTap
import me.vistark.fastdroid.utils.ViewExtension.onTextChanged

class SpeciesInfoProviderActivity :
    FastdroidActivity(R.layout.activity_species_info_provider, isCanAutoTranslate = true) {

    val currentSpeciesSync = SpeciesSync()

    val pickedImagesUris = ArrayList<Uri>()
    lateinit var adapter: PickedImagePreviewAdapter

    var selectedSpeciesId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Nếu thu thập dữ liệu đã có không thành công thì tiến hành kết thúc màn hình hiện tại
        if (!collectReceiveDatas())
            return

        initEvents()

        pickedImages()

        rlContainer.scaleUpCenter()


    }

    private fun pickedImages() {
        rvPickedImages.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rvPickedImages.layoutManager = layoutManager

        adapter = PickedImagePreviewAdapter(pickedImagesUris)
        adapter.onDelete = {
            pickedImagesUris.removeAt(it)
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
                MultipleLanguage.L(getString(R.string.CanNotGetSpeciesCategory)),
                Toasty.LENGTH_SHORT,
                true
            ).show()
            onBackPressed()
            return false
        }

        currentSpeciesSync.specieId = selectedSpeciesId

        return true
    }

    private fun initEvents() {
        rlRoot.setOnClickListener {
            animateFinish()
        }

        asIvBackIcon.onTap {
            animateFinish()
        }
        asIvAddImage.onTap {
            // Pick image/Take photo
            pickImageForUri {
                pickedImagesUris.add(it)
                updatePickedImages()
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
                // Nếu đã hợp lệ
                if (HomeActivity.PUBLIC_CURRENT_COORDINATES == null) {
                    Toasty.error(
                        this,
                        L(getString(R.string.PleaseWaitForAFewMinutesSystemIsDetectYourLocationCoordinates)),
                        Toasty.LENGTH_SHORT,
                        true
                    ).show()
                } else {
                    // Nếu đã có vị trí
                    currentSpeciesSync.long =
                        HomeActivity.PUBLIC_CURRENT_COORDINATES!!.longitude.toString()
                    currentSpeciesSync.lat =
                        HomeActivity.PUBLIC_CURRENT_COORDINATES!!.latitude.toString()

                    // Tiến hành xử lý và lưu (hình ảnh & dữ liệu)
                    saveProcessing()
                }
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

        Thread {
            // Lưu danh sách các ảnh này vào cache
            pickedImagesUris.forEach { uri ->
                uri.saveImage(this).apply {
                    if (this.isNotEmpty()) {
                        snapshotPickedImages.add(this)
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
        if (pickedImagesUris.isEmpty() ||
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
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}