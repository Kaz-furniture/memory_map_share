package com.kaz_furniture.memoryMapShare.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.databinding.ActivityPasswordForgetBinding
import com.kaz_furniture.memoryMapShare.viewModel.PasswordForgetViewModel

class PasswordForgetActivity: BaseActivity() {
    lateinit var binding: ActivityPasswordForgetBinding
    private val viewModel: PasswordForgetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_password_forget)
        binding.lifecycleOwner = this
        binding.email = viewModel.emailInput
        binding.submitButton.setOnClickListener {
            viewModel.sendPasswordReset(this)
        }
        viewModel.canSubmit.observe(this, Observer {
            binding.canSubmit = it
        })
        viewModel.emailError.observe(this, Observer {
            binding.emailError = it
        })
        title = "パスワード再設定"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    companion object {
        fun start(activity: Activity) {
            activity.apply {
                startActivity(Intent(activity, PasswordForgetActivity::class.java))
            }
        }
    }
}