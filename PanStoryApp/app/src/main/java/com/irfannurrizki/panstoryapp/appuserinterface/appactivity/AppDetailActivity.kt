package com.irfannurrizki.panstoryapp.appuserinterface.appactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.irfannurrizki.panstoryapp.R
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.ListStoryItem
import com.irfannurrizki.panstoryapp.databinding.ActivityAppDetailBinding

class AppDetailActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAppDetailBinding
    companion object{
        const val EXTRA_DETAIL = "extra_detail"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        val detail = intent.getParcelableExtra<ListStoryItem>(EXTRA_DETAIL)

        binding.apply {
            tvStorydetailName.text = detail?.name
            tvStorydetailDescription.text = detail?.description
        }
        Glide.with(this)
            .load(detail?.photoUrl)
            .into(binding.imStorydetailPhoto)
    }
}
