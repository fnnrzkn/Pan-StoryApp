package com.irfannurrizki.panstoryapp.appuserinterface.appadapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat

import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.irfannurrizki.panstoryapp.R
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.ListStoryItem
import com.irfannurrizki.panstoryapp.appuserinterface.appactivity.AppDetailActivity
import com.irfannurrizki.panstoryapp.databinding.StoryItemBinding

class AppListStoryAdapter : PagingDataAdapter<ListStoryItem, AppListStoryAdapter.StoryViewHolder>(
    DIFF_CALLBACK
) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val listStoryItem = getItem(position)
        if (listStoryItem != null) {
            holder.bind(listStoryItem)
        }
    }


    inner class StoryViewHolder(private val binding: StoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listStoryItem: ListStoryItem) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(listStoryItem.photoUrl)
                    .into(binding.imStoryPhoto)
               tvStoryName.text = listStoryItem.name
            }
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, AppDetailActivity::class.java).apply {
                    putExtra(AppDetailActivity.EXTRA_DETAIL, listStoryItem)
                }
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        androidx.core.util.Pair(binding.imStoryPhoto, "detail_photo"),
                        androidx.core.util.Pair(binding.tvStoryName, "detail_name"),
                    )

                it.context.startActivity(intent,optionsCompat.toBundle())
            }
        }
    }
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
