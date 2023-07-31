package com.irfannurrizki.panstoryapp.appuserinterface.appadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.irfannurrizki.panstoryapp.databinding.AppLoadingItemBinding

class AppLoadingStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<AppLoadingStateAdapter.LoadingStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingStateViewHolder {
        val binding = AppLoadingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class LoadingStateViewHolder(private val binding: AppLoadingItemBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButtonLoading.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMsgLoading.text = loadState.error.localizedMessage
            }
            binding.progressBarLoading.isVisible = loadState is LoadState.Loading
            binding.retryButtonLoading.isVisible = loadState is LoadState.Error
            binding.errorMsgLoading.isVisible = loadState is LoadState.Error
        }
    }
}
