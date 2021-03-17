package com.kaz_furniture.memoryMapShare.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.ActivityLoginBinding
import com.kaz_furniture.memoryMapShare.viewModel.LoginViewModel

class LoginActivity: BaseActivity() {

    lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.createUserTextView.setOnClickListener {
            launchCreateAccountActivity()
        }
        binding.passwordForget.setOnClickListener {
            PasswordForgetActivity.start(this)
        }
        binding.email = viewModel.email
        binding.password = viewModel.password
        binding.login.setOnClickListener {
            viewModel.login(this,this@LoginActivity)
        }
        viewModel.canSubmit.observe(this, Observer {
            binding.canSubmit = it
        })
        viewModel.emailError.observe(this, Observer {
            binding.emailError = it
        })
        viewModel.passwordError.observe(this, Observer {
            binding.passwordError = it
        })
        viewModel.loginSuccess.observe(this, Observer {
            finish()
        })

        binding.emailInput.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                hideKeyboard(v)
            }
        }
        binding.container.setOnClickListener{
            hideKeyboard(it)
        }
        binding.passwordInput.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                hideKeyboard(v)
            }
        }
    }

    private fun launchCreateAccountActivity() {
        val intent = CreateAccountActivity.newIntent(this)
        startActivityForResult(intent, REQUEST_CODE_CREATE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) finish()
    }

    companion object {
        private const val REQUEST_CODE_CREATE = 1001
        fun start(activity: Activity) =
            activity.apply {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
    }
}