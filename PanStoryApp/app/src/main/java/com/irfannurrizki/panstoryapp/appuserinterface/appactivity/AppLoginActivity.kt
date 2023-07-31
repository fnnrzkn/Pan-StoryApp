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
import androidx.dynamicanimation.animation.DynamicAnimation.ALPHA
import androidx.lifecycle.ViewModelProvider
import com.irfannurrizki.panstoryapp.R
import com.irfannurrizki.panstoryapp.appdata.apprepo.AppMainRepository
import com.irfannurrizki.panstoryapp.appdata.localdata.AppUserSession
import com.irfannurrizki.panstoryapp.appdata.localdata.applocaldatabase.AppUserModel
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.LoginResponse
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.LoginResult
import com.irfannurrizki.panstoryapp.appdata.serverdata.apisetting.AppApiConfig
//import com.irfannurrizki.panstoryapp.apphelper.dataStore
import com.irfannurrizki.panstoryapp.appuserinterface.appcustomview.CustomEmailEditText
import com.irfannurrizki.panstoryapp.appuserinterface.appcustomview.CustomLoadDialog
import com.irfannurrizki.panstoryapp.appuserinterface.appcustomview.CustomPasswordEditText
import com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel.AppLoginViewModel
import com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel.AppViewModelFactory
import com.irfannurrizki.panstoryapp.databinding.ActivityAppLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.xml.datatype.DatatypeConstants.DURATION

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AppLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppLoginBinding
    private lateinit var loginViewModel: AppLoginViewModel
    protected var customLoadDialog: CustomLoadDialog? = null
    private lateinit var loginResponse: LoginResponse
    private lateinit var customEmailEditText: CustomEmailEditText
    private lateinit var customPasswordEditText: CustomPasswordEditText
    private var correctEmail = false
    private var correctPassword = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val loginButton=binding.loginButton
        customEmailEditText = binding.etEmailinput
        customPasswordEditText = binding.etPasswordinput

        if (!intent.getStringExtra("email").isNullOrEmpty()) {
            customEmailEditText.setText(intent.getStringExtra("email"))
            correctEmail = true
        }
        if (!intent.getStringExtra("password").isNullOrEmpty()) {
            customPasswordEditText.setText(intent.getStringExtra("password"))
            correctPassword = true
        }

        fun setLoginButtonEnable() {
            loginButton.isEnabled = correctEmail && correctPassword
        }

        customEmailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                correctEmail =
                    !s.isNullOrEmpty() && emailRegex.matches(s.toString())
                setLoginButtonEnable()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        customPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                correctPassword = !(!s.isNullOrEmpty() && s.length < 8)
                setLoginButtonEnable()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        setLoginButtonEnable()
        setupAction()
        setupViewModel()
        setupView()
        playAnimation()
    }

    private fun playAnimation() {
        val tvwelcomeLogin =
            ObjectAnimator.ofFloat(binding.tvWelcomelogin, View.ALPHA, ALPHA ).setDuration(DURATION)
        val tvemailColumn =
            ObjectAnimator.ofFloat(binding.tvEmailcolumn, View.ALPHA, ALPHA).setDuration(DURATION)
        val etemailInput =
            ObjectAnimator.ofFloat(binding.etEmailinput, View.ALPHA, ALPHA).setDuration(DURATION)
        val tvpasswordColumn=
            ObjectAnimator.ofFloat(binding.tvPasswordcolumn, View.ALPHA, ALPHA).setDuration(DURATION)
        val etpasswordInput =
            ObjectAnimator.ofFloat(binding.etPasswordinput, View.ALPHA, ALPHA).setDuration(DURATION)
        val loginButton =
            ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, ALPHA).setDuration(DURATION)
        val tvhaventAccount =
            ObjectAnimator.ofFloat(binding.tvHaventaccount, View.ALPHA, ALPHA).setDuration(DURATION)

        AnimatorSet().apply {
            playSequentially(
                tvwelcomeLogin,
                tvemailColumn,
                etemailInput,
                tvpasswordColumn,
                etpasswordInput,
                loginButton,
                tvhaventAccount
            )
            start()
        }
    }

    private fun setupView() {
        customLoadDialog = CustomLoadDialog(this)

        loginViewModel.isLogin().observe(this) {
            if (it){
                val intent = Intent(this@AppLoginActivity, AppMainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.etEmailinput.text.toString()
            val password = binding.etPasswordinput.text.toString()
            when {
                email.isEmpty() -> {
                    binding.tvEmailcolumn.error = "Masukkan email"
                    when{email.isNotEmpty() -> {
                        binding.tvEmailcolumn
                    }}
                }
                password.isEmpty() -> {
                    binding.tvPasswordcolumn.error = "Masukkan password"
                    when{email.isNotEmpty() -> {
                        binding.tvPasswordcolumn
                    }}
                }
                else -> {
                    loginViewModel.login(email, password).observe(this){
                        if (it != null){
                            when(it){
                                is com.irfannurrizki.panstoryapp.appdata.apprepo.AppResult.Loading -> {
                                    showLoading()
                                }
                                is com.irfannurrizki.panstoryapp.appdata.apprepo.AppResult.Success -> {
                                    hideLoading()
                                    loginResponse = it.data
                                    if (loginResponse.error == true){
                                        Toast.makeText(this@AppLoginActivity, loginResponse.message, Toast.LENGTH_SHORT).show()
                                    } else {
                                        loginViewModel.saveUser(
                                            AppUserModel(
                                                loginResponse.loginResult!!.userId,
                                                loginResponse.loginResult!!.name,
                                                loginResponse.loginResult!!.token)
                                        )
                                        loginViewModel.login()
                                        loginViewModel.saveToken(loginResponse.loginResult!!.token)
                                    }
                                }
                                is com.irfannurrizki.panstoryapp.appdata.apprepo.AppResult.Error -> {
                                    hideLoading()
                                    Toast.makeText(this@AppLoginActivity, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.tvHaventaccount.setOnClickListener {
            val intent = Intent(this@AppLoginActivity, AppRegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            AppViewModelFactory(AppMainRepository.getInstance(dataStore, AppApiConfig.getAppApiService()))
        )[AppLoginViewModel::class.java]
    }

    private fun showLoading(){
        if (!customLoadDialog?.isShowing!!){
            customLoadDialog?.show()
        }
    }

    private fun hideLoading(){
        if (customLoadDialog?.isShowing!!){
            customLoadDialog?.dismiss()
        }
    }
    companion object {
        private const val DURATION = 250L
        private const val ALPHA = 2f
        val emailRegex: Regex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+\$")
    }
}