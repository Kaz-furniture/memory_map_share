package com.kaz_furniture.memoryMapShare.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.Marker
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.applicationContext
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.activity.AlbumActivity
import com.kaz_furniture.memoryMapShare.databinding.ListMarkerBinding
import com.kaz_furniture.memoryMapShare.databinding.ListMarkerEmptyBinding
import com.kaz_furniture.memoryMapShare.viewModel.AlbumViewModel
import com.kaz_furniture.memoryMapShare.viewModel.MyPageViewModel
import timber.log.Timber

class MarkerListView: RecyclerView {

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

        private val viewModel: MyPageViewModel by(context as ComponentActivity).viewModels()
        private val items = mutableListOf<com.kaz_furniture.memoryMapShare.data.Marker>()

        fun refresh(list: List<com.kaz_furniture.memoryMapShare.data.Marker>) {
            items.apply {
                clear()
                addAll(list)
            }
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
                else -> ItemViewHolder(ListMarkerBinding.inflate(LayoutInflater.from(context), parent, false))
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
                marker = data
                locationName.text = data.locationName
                memoDisplay.text = data.memo
                memoryTime.text = android.text.format.DateFormat.format(applicationContext.getString(R.string.date), data.memoryTime)
                imageView1.setOnClickListener {
                    viewModel.launchAlbumActivity(data.imageIdList)
                }
                imageView2.setOnClickListener {
                    viewModel.launchAlbumActivity(data.imageIdList)
                }
                imageView3.setOnClickListener {
                    viewModel.launchAlbumActivity(data.imageIdList)
                }

            }
            when {
                data.imageIdList.size <= 1 -> holder.binding.moreHorizon.visibility = View.INVISIBLE
                data.imageIdList.size == 2 -> {
                    holder.binding.apply {
                        imageView2.visibility = View.VISIBLE
                        moreHorizon.visibility = View.INVISIBLE
                    }
                }
                data.imageIdList.size == 3 -> {
                    holder.binding.apply {
                        moreHorizon.visibility = View.INVISIBLE
                        imageView2.visibility = View.VISIBLE
                        imageView3.visibility = View.VISIBLE
                    }
                }
                data.imageIdList.size > 3 -> {
                    holder.binding.apply {
                        imageView2.visibility = View.VISIBLE
                        imageView3.visibility = View.VISIBLE
                    }
                }
            }
        }

        class ItemViewHolder(val binding: ListMarkerBinding): RecyclerView.ViewHolder(binding.root)
        class EmptyViewHolder(val binding: ListMarkerEmptyBinding): RecyclerView.ViewHolder(binding.root)
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}