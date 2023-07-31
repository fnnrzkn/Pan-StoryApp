package com.irfannurrizki.panstoryapp.appuserinterface.appactivity

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.irfannurrizki.panstoryapp.appdata.apprepo.AppMainRepository
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.ListStoryItem
import com.irfannurrizki.panstoryapp.appdata.serverdata.apisetting.AppApiConfig
import com.irfannurrizki.panstoryapp.appuserinterface.appactivity.mapactivity.AppMapActivity
import com.irfannurrizki.panstoryapp.appuserinterface.appadapter.AppListStoryAdapter
import com.irfannurrizki.panstoryapp.appuserinterface.appadapter.AppLoadingStateAdapter
import com.irfannurrizki.panstoryapp.appuserinterface.appcustomview.CustomLoadDialog
import com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel.AppLoginViewModel
import com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel.AppMainViewModel
import com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel.AppMainViewModelFactory

import com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel.AppViewModelFactory
import com.irfannurrizki.panstoryapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var arrayListStories: ArrayList<ListStoryItem>
    private val appMainViewModel: AppMainViewModel by viewModels {
        AppMainViewModelFactory(this)
    }
    private val appLoginViewModel: AppLoginViewModel by viewModels {
        AppViewModelFactory(AppMainRepository.getInstance(dataStore, AppApiConfig.getAppApiService()))
    }
    private lateinit var appListStoryAdapter: AppListStoryAdapter
    protected var customLoadDialog: CustomLoadDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.rvStories.layoutManager = LinearLayoutManager(this)

        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.actionStoryLocationFeature.setOnClickListener {
            val intent = Intent(this@AppMainActivity, AppMapActivity::class.java)
            arrayListStories = ArrayList<ListStoryItem>()
            intent.putExtra(ARRAY_LIST_STORIES, arrayListStories)
            startActivity(intent)
        }

        binding.actionAddStoryFeature.setOnClickListener {
            val intent = Intent(this@AppMainActivity, AppAddStoryActivity::class.java)
            startActivity(intent)
        }

        binding.actionLogoutFeature.setOnClickListener {
            appLoginViewModel.logout()
            val intent = Intent(this@AppMainActivity, AppLoginActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        binding.actionSettingFeature.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    private fun setupView() {
        appListStoryAdapter = AppListStoryAdapter()

        with(binding.rvStories) {
            setHasFixedSize(true)
            adapter = appListStoryAdapter.withLoadStateHeaderAndFooter(
                header = AppLoadingStateAdapter { appListStoryAdapter.retry() },
                footer = AppLoadingStateAdapter { appListStoryAdapter.retry() }
            )
        }

        appLoginViewModel.getToken().observe(this) { token ->
            appMainViewModel.getStories("Bearer $token").observe(this) { pagingData ->
                lifecycleScope.launch {
                    appListStoryAdapter.submitData(pagingData)
                }
            }
        }


    }
    companion object {
        const val ARRAY_LIST_STORIES = "array_list_stories"
    }
}
