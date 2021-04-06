package com.kaz_furniture.memoryMapShare.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.ActivityEditMarkerBinding
import com.kaz_furniture.memoryMapShare.viewModel.EditMarkerViewModel
import java.util.*

class EditMarkerActivity: BaseActivity() {
    private val viewModel: EditMarkerViewModel by viewModels()
    lateinit var binding: ActivityEditMarkerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_marker)
        binding.lifecycleOwner = this
        binding.locationName = viewModel.locationNameInput
        binding.memo = viewModel.memoInput
        intent.getStringExtra(KEY_NAME)?.also {
            viewModel.locationNameInput.value = it
        } ?:return
        intent.getStringExtra(KEY_MEMO)?.also {
            viewModel.memoInput.value = it
        } ?:return
        intent.getStringExtra(KEY_DATE)?.also {
            binding.timeDateDisplay.text = it
        } ?:return

        binding.dateSelectButton.setOnClickListener {
            launchDateSelectDialog()
        }
        binding.submitButton.setOnClickListener {
            viewModel.submitMarker(intent.getStringExtra(KEY_MARKER_ID) ?:return@setOnClickListener)
            setResult(RESULT_OK, Intent().apply {
                putExtra(KEY_NAME, viewModel.locationNameInput.value)
                putExtra(KEY_MEMO_BACK, viewModel.memoInput.value)
                putExtra(KEY_DATE_BACK, android.text.format.DateFormat.format(getString(R.string.date), viewModel.newDate) ?:binding.timeDateDisplay.text.toString())
            })
            finish()
        }

        title = getString(R.string.edit_marker2)
    }

    private fun launchDateSelectDialog() {
        MaterialDialog(this).show {
            datePicker { _, date ->
                binding.timeDateDisplay.text = android.text.format.DateFormat.format(getString(R.string.date), date)
                viewModel.newDate = date.time
            }
        }
    }

    companion object {
        private const val KEY_NAME = "key_name"
        private const val KEY_MEMO = "key_memo"
        private const val KEY_MEMO_BACK = "key_memo_back"
        private const val KEY_DATE = "key_date"
        private const val KEY_DATE_BACK = "key_date_back"
        private const val KEY_MARKER_ID = "key_marker_id"
        fun newIntent(activity: Activity, locationName: String, memo: String, date: String, markerId: String): Intent =
            Intent(activity, EditMarkerActivity::class.java).apply {
                putExtra(KEY_NAME, locationName)
                putExtra(KEY_MEMO, memo)
                putExtra(KEY_DATE, date)
                putExtra(KEY_MARKER_ID, markerId)
            }
    }
}