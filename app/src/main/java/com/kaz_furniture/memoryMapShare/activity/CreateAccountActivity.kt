package com.kaz_furniture.memoryMapShare.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.ActivityCreateAccountBinding
import com.kaz_furniture.memoryMapShare.viewModel.CreateAccountViewModel

class CreateAccountActivity: BaseActivity() {


    lateinit var binding: ActivityCreateAccountBinding
    private val viewModel: CreateAccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityCreateAccountBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_account)
        binding.lifecycleOwner = this
        binding.email = viewModel.email
        binding.password = viewModel.password
        binding.passwordConfirm = viewModel.passwordValidate
        binding.name = viewModel.nameInput
        viewModel.canSubmit.observe(this, androidx.lifecycle.Observer {
            binding.canSubmit = it
        })
        viewModel.nameError.observe(this, androidx.lifecycle.Observer {
            binding.nameError = it
        })
        viewModel.emailError.observe(this, androidx.lifecycle.Observer {
            binding.emailError = it
        })
        viewModel.passwordError.observe(this, androidx.lifecycle.Observer{
            binding.passwordError = it
        })
        viewModel.userCreated.observe(this, Observer {
            setResult(RESULT_OK)
            finish()
        })
        binding.saveButton.setOnClickListener{
            viewModel.createAuthUser()
        }
        binding.userNameEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                hideKeyboard(v)
            }
        }
        binding.container.setOnClickListener{
            hideKeyboard(it)
        }
        binding.emailEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                hideKeyboard(v)
            }
        }
        binding.passwordEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                hideKeyboard(v)
            }
        }
        binding.passwordConfirmEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                hideKeyboard(v)
            }
        }
        title = getString(R.string.createAccount)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }


    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, CreateAccountActivity::class.java)
        }
    }
}