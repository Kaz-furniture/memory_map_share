package com.kaz_furniture.memoryMapShare.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlbumViewModel: ViewModel() {
    val imageClickedLiveData = MutableLiveData<String>()

    fun imageClick(imageId: String) {
        imageClickedLiveData.postValue(imageId)
    }
}