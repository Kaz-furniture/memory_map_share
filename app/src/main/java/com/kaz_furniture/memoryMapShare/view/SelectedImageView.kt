package com.kaz_furniture.memoryMapShare.view

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kaz_furniture.memoryMapShare.databinding.ListEmptyImageBinding
import com.kaz_furniture.memoryMapShare.databinding.ListSelectedImageBinding
import com.kaz_furniture.memoryMapShare.viewModel.CreateMarkerViewModel

class SelectedImageView: RecyclerView {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr)

    val customAdapter by lazy { Adapter(context) }

    init {
        adapter = customAdapter
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
    }

    class Adapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val viewModel: CreateMarkerViewModel by(context as ComponentActivity).viewModels()
        private val items = mutableListOf<Uri>()

        fun refresh(list: List<Uri>) {
            items.apply {
                clear()
                addAll(list)
            }
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = if (items.isNotEmpty()) items.size else 1

        override fun getItemViewType(position: Int): Int {
            return if (items.isNotEmpty()) VIEW_TYPE_ITEM else VIEW_TYPE_EMPTY
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            when (viewType) {
                VIEW_TYPE_EMPTY -> EmptyViewHolder(ListEmptyImageBinding.inflate(LayoutInflater.from(context), parent, false))
                else -> ItemViewHolder(ListSelectedImageBinding.inflate(LayoutInflater.from(context), parent, false))
            }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            when(holder) {
                is ItemViewHolder -> onBindViewHolder(holder, position)
            }
        }

        private fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val data = items[position]
            val inputStream = context.contentResolver?.openInputStream(data)
            val image = BitmapFactory.decodeStream(inputStream)
            holder.binding.selectedImage.setImageBitmap(image)
        }

        class ItemViewHolder(val binding: ListSelectedImageBinding): RecyclerView.ViewHolder(binding.root)
        class EmptyViewHolder(val binding: ListEmptyImageBinding): RecyclerView.ViewHolder(binding.root)
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}