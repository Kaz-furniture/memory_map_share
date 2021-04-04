package com.kaz_furniture.memoryMapShare.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.data.User
import com.kaz_furniture.memoryMapShare.databinding.ListGroupMemberBinding
import com.kaz_furniture.memoryMapShare.databinding.ListMarkerEmptyBinding
import com.kaz_furniture.memoryMapShare.viewModel.CreateGroupViewModel
import com.kaz_furniture.memoryMapShare.viewModel.EditGroupViewModel

class GroupMemberEditView: RecyclerView {

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr)

    val customAdapter by lazy { Adapter(context) }

    init {
        adapter = customAdapter
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
    }

    class Adapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val viewModel: EditGroupViewModel by(context as ComponentActivity).viewModels()
        private val items = mutableListOf<User>()

        fun refresh(list: List<User>) {
            items.apply {
                clear()
                addAll(list)
            }
            viewModel.userAndCheckedList.clear()
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int =
            if (items.isNotEmpty()) items.size else 1

        override fun getItemViewType(position: Int): Int {
            return if (items.isNotEmpty()) VIEW_TYPE_ITEM else VIEW_TYPE_EMPTY
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return when (viewType) {
                VIEW_TYPE_EMPTY -> EmptyViewHolder(ListMarkerEmptyBinding.inflate(LayoutInflater.from(context), parent, false))
                else -> ItemViewHolder(ListGroupMemberBinding.inflate(LayoutInflater.from(context), parent, false))
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            when(holder) {
                is ItemViewHolder -> onBindViewHolder(holder, position)
            }
        }

        private fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val data = items[position]
            holder.binding.apply {
                userForIcon = data
                userId.text = MemoryMapShareApplication.applicationContext.getString(R.string.userIdDisplay, data.userId)
                userName.text = data.name
                val initUserAndChecked = UserAndChecked().apply {
                    userId = data.userId
                    checked = checkBox.isChecked
                }
                viewModel.userAndCheckedList.add(initUserAndChecked)
                checkBox.isChecked = data.groupIds.contains(viewModel.groupId)
                checkBox.setOnClickListener {
                    val userAndChecked = UserAndChecked().apply {
                        userId = data.userId
                        checked = checkBox.isChecked
                    }
                    viewModel.userAndCheckedList.apply {
                        removeAll { it.userId == data.userId }
                        add(userAndChecked)
                    }
//                    Timber.d("isChecked = ${viewModel.userAndCheckedList.map { it.checked }}")
                }
            }
        }
        class UserAndChecked {
            var userId = ""
            var checked = false
        }

        class ItemViewHolder(val binding: ListGroupMemberBinding): RecyclerView.ViewHolder(binding.root)
        class EmptyViewHolder(val binding: ListMarkerEmptyBinding): RecyclerView.ViewHolder(binding.root) {
            init {
                binding.emptyText.setText(R.string.noFriend)
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}