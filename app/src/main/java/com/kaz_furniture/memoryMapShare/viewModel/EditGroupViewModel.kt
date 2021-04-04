package com.kaz_furniture.memoryMapShare.viewModel

import androidx.lifecycle.MutableLiveData
import com.kaz_furniture.memoryMapShare.view.GroupMemberEditView

class EditGroupViewModel: BaseViewModel() {
    var groupId = ""
    val groupNameInput = MutableLiveData<String>()
    val userAndCheckedList = ArrayList<GroupMemberEditView.Adapter.UserAndChecked>()
}