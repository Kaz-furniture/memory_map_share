package com.kaz_furniture.memoryMapShare.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.FragmentImageDisplayBinding
import com.kaz_furniture.memoryMapShare.viewModel.AlbumViewModel

class ImageDisplayFragment: Fragment(R.layout.fragment_image_display) {
    private var binding: FragmentImageDisplayBinding? = null
//    private val viewModel: AlbumViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view) ?: return
        binding?.lifecycleOwner = this
        binding?.imageId = requireArguments().getString(KEY_ID)
    }

    companion object {
        private const val KEY_ID = "key_id"
        val tag: String = ImageDisplayFragment::class.java.simpleName
        fun newInstance(imageId: String): ImageDisplayFragment {
            val args = Bundle().apply {
                putString(KEY_ID, imageId)
            }
            return ImageDisplayFragment().apply {
                arguments = args
            }
        }
    }
}