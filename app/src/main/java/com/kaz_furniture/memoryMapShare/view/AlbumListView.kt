package com.kaz_furniture.memoryMapShare.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kaz_furniture.memoryMapShare.databinding.CellAlbumViewBinding
import com.kaz_furniture.memoryMapShare.viewModel.AlbumViewModel

class AlbumListView: RecyclerView {

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr)

    val customAdapter by lazy { Adapter(context) }

    init {
        adapter = customAdapter
        setHasFixedSize(true)
        layoutManager = GridLayoutManager(context, MAX_SPAN_COUNT)
    }

    class Adapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val viewModel: AlbumViewModel by (context as ComponentActivity).viewModels()
        private val items = mutableListOf<String>()

        fun refresh(list: List<String>) {
            items.apply {
                clear()
                addAll(list)
            }
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = items.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
                ItemViewHolder(CellAlbumViewBinding.inflate(LayoutInflater.from(context), parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            when(holder) {
                is ItemViewHolder -> onBindViewHolder(holder, position)
            }
        }

        private fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val data = items[position]
            holder.binding.apply {
                imageId = data
                imageCell.setOnClickListener {
                    viewModel.imageClick(data)
                }
            }
        }
        class ItemViewHolder(val binding: CellAlbumViewBinding): RecyclerView.ViewHolder(binding.root)
    }

    companion object {
        private const val MAX_SPAN_COUNT = 2
    }
}