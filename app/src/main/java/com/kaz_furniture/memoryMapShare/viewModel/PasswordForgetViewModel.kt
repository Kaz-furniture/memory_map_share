package com.kaz_furniture.memoryMapShare.viewModel

import android.app.Activity
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.applicationContext
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.activity.PasswordForgetActivity

class PasswordForgetViewModel: ViewModel() {
    var emailInput = MutableLiveData<String>()
    var canSubmit = MediatorLiveData<Boolean>().also { result ->
        result.addSource(emailInput) { result.value = submitValidation() }
    }
    var emailError = MutableLiveData<String>()

    fun sendPasswordReset(activity: PasswordForgetActivity) {
        val emailValue = emailInput.value ?:""
        FirebaseAuth.getInstance().sendPasswordResetEmail(emailValue)
            .addOnCompleteListener {
                activity.setResult(Activity.RESULT_OK)
                activity.finish()
                Toast.makeText(applicationContext, "パスワード再設定用のメールを送信しました", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
            }
    }

    private fun submitValidation(): Boolean {
        val emailValue = emailInput.value
        return if (emailValue == null || emailValue.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()){
            emailError.postValue(applicationContext.getString(R.string.inputCorrectly))
            false
        } else {
            true
        }
    }
}