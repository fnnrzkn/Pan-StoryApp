package com.irfannurrizki.panstoryapp.appuserinterface.appactivity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.irfannurrizki.panstoryapp.R
import com.irfannurrizki.panstoryapp.appdata.apprepo.AppMainRepository
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.RegisterResponse
import com.irfannurrizki.panstoryapp.appdata.serverdata.apisetting.AppApiConfig
//import com.irfannurrizki.panstoryapp.apphelper.dataStore
import com.irfannurrizki.panstoryapp.appuserinterface.appcustomview.CustomEmailEditText
import com.irfannurrizki.panstoryapp.appuserinterface.appcustomview.CustomLoadDialog
import com.irfannurrizki.panstoryapp.appuserinterface.appcustomview.CustomNameEditText
import com.irfannurrizki.panstoryapp.appuserinterface.appcustomview.CustomPasswordEditText
import com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel.AppRegisterViewModel
import com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel.AppViewModelFactory
import com.irfannurrizki.panstoryapp.databinding.ActivityAppRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AppRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppRegisterBinding
    private lateinit var appRegisterViewModel: AppRegisterViewModel
    protected var customLoadDialog: CustomLoadDialog? = null
    private lateinit var registerResponse: RegisterResponse

    private lateinit var customEmailEditText: CustomEmailEditText
    private lateinit var customPasswordEditText: CustomPasswordEditText
    private lateinit var customNameEditText: CustomNameEditText

    private var correctEmail: Boolean = false
    private var correctPassword: Boolean = false
    private var correctName: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val registButton = binding.registButton
        customEmailEditText = binding.etEmailinput
        customPasswordEditText = binding.etPasswordinput
        customNameEditText = binding.etNameinput

        customNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    correctName = s.length >= 2
                }
                registButton.isEnabled = correctName && correctEmail && correctPassword
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        customEmailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                correctEmail =
                    !s.isNullOrEmpty() && AppLoginActivity.emailRegex.matches(s.toString())
                registButton.isEnabled = correctName && correctEmail && correctPassword
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        customPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                correctPassword = !(!s.isNullOrEmpty() && s.length < 8)
                registButton.isEnabled = correctName && correctEmail && correctPassword
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.tvHaveaccount.setOnClickListener {
            val i = Intent(this, AppLoginActivity::class.java)
            startActivity(i)
            finish()
        }

        setupViewModel()
        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupViewModel() {
        appRegisterViewModel = ViewModelProvider(
            this,
            AppViewModelFactory(
                AppMainRepository.getInstance(dataStore,AppApiConfig.getAppApiService()
                )
            )
        )[AppRegisterViewModel::class.java]
    }


    private fun playAnimation() {
        val tvRegisterBar = ObjectAnimator.ofFloat(binding.tvRegisterbar, View.ALPHA, ALPHA).setDuration(
            DURATION
        )
        val tvNameColumn = ObjectAnimator.ofFloat(binding.tvNamecolumn, View.ALPHA, ALPHA).setDuration(
            DURATION
        )
        val etNameInput = ObjectAnimator.ofFloat(binding.etNameinput, View.ALPHA, ALPHA).setDuration(
            DURATION
        )
        val tvEmailColumn = ObjectAnimator.ofFloat(binding.tvEmailcolumn, View.ALPHA, ALPHA).setDuration(
            DURATION
        )
        val etEmailInput = ObjectAnimator.ofFloat(binding.etEmailinput, View.ALPHA, ALPHA).setDuration(
            DURATION
        )
        val tvPasswordColumn = ObjectAnimator.ofFloat(binding.tvPasswordcolumn, View.ALPHA, ALPHA).setDuration(
            DURATION
        )
        val etPasswordInput = ObjectAnimator.ofFloat(binding.etPasswordinput, View.ALPHA, ALPHA).setDuration(
            DURATION
        )
        val registButton = ObjectAnimator.ofFloat(binding.registButton, View.ALPHA, ALPHA).setDuration(
            DURATION
        )
        val tvHaveAccount = ObjectAnimator.ofFloat(binding.tvHaveaccount, View.ALPHA, ALPHA).setDuration(
            DURATION
        )

        AnimatorSet().apply {
            playSequentially(
                tvRegisterBar,
                tvNameColumn,
                etNameInput,
                tvEmailColumn,
                etEmailInput,
                tvPasswordColumn,
                etPasswordInput,
                registButton,
                tvHaveAccount
            )
            start()
        }
    }

        private fun setupView() {
            customLoadDialog = CustomLoadDialog(this)
        }

        private fun setupAction() {
            binding.registButton.setOnClickListener {
                val name = binding.etNameinput.text.toString()
                val email = binding.etEmailinput.text.toString()
                val password = binding.etPasswordinput.text.toString()
                when {
                    name.isEmpty() -> {
                        binding.tvNamecolumn.error = "Masukkan name"
                    }
                    email.isEmpty() -> {
                        binding.tvEmailcolumn.error = "Masukkan email"
                    }
                    password.isEmpty() -> {
                        binding.tvPasswordcolumn.error = "Masukkan password"
                    }
                    else -> {
                        appRegisterViewModel.register(name, email, password).observe(this) {
                            if (it != null) {
                                when (it) {
                                    is com.irfannurrizki.panstoryapp.appdata.apprepo.AppResult.Loading -> {
                                        showLoading()
                                    }
                                    is com.irfannurrizki.panstoryapp.appdata.apprepo.AppResult.Success -> {
                                        hideLoading()
                                        registerResponse = it.data
                                        if (registerResponse.error == true) {
                                            Toast.makeText(
                                                this@AppRegisterActivity,
                                                registerResponse.message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            val intent = Intent(
                                                this@AppRegisterActivity,
                                                AppLoginActivity::class.java
                                            )
                                            startActivity(intent)
                                        }
                                    }
                                    is com.irfannurrizki.panstoryapp.appdata.apprepo.AppResult.Error -> {
                                        hideLoading()
                                        Toast.makeText(
                                            this@AppRegisterActivity,
                                            getString(R.string.register_failed),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        private fun showLoading() {
            if (!customLoadDialog?.isShowing!!) {
                customLoadDialog?.show()
            }
        }

        private fun hideLoading() {
            if (customLoadDialog?.isShowing!!) {
                customLoadDialog?.dismiss()
            }
        }
        companion object {
        private const val DURATION = 200L
        private const val ALPHA = 1f
        }
    }
