package com.kaz_furniture.memoryMapShare.viewModel

import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.applicationContext
import com.kaz_furniture.memoryMapShare.MemoryMapShareApplication.Companion.myUser
import com.kaz_furniture.memoryMapShare.R
import com.kaz_furniture.memoryMapShare.activity.CreateAccountActivity
import com.kaz_furniture.memoryMapShare.data.User

class CreateAccountViewModel: ViewModel() {

    val userCreated = MutableLiveData<Boolean>()

    val nameInput = MutableLiveData<String>()
    val nameError = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val emailError = MutableLiveData<String>()
    val passwordError = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val passwordValidate = MutableLiveData<String>()
    val canSubmit = MediatorLiveData<Boolean>().also { result ->
        result.addSource(nameInput) { result.value = submitValidation()}
        result.addSource(email) { result.value = submitValidation()}
        result.addSource(password) { result.value = submitValidation()}
        result.addSource(passwordValidate) { result.value = submitValidation()}
    }

    private fun submitValidation(): Boolean {
        return validateName() && validateEmail() && validatePassword()
    }

    private fun validateName(): Boolean {
        val nameValue = nameInput.value
        return if (nameValue == null || nameValue.isBlank()) {
            nameError.postValue(applicationContext.getString(R.string.inputCorrectly))
            false
        } else {
            true
        }
    }

    private fun validateEmail(): Boolean {
        val emailValue = email.value
        return if (emailValue == null || emailValue.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()){
            emailError.postValue(applicationContext.getString(R.string.inputCorrectly))
            false
        } else {
            true
        }
    }

    private fun validatePassword(): Boolean {
        val passwordValue = password.value
        return if (passwordValue == null || passwordValue.isBlank() || passwordValue != passwordValidate.value || passwordValue.length < 8) {
            passwordError.postValue(applicationContext.getString(R.string.inputCorrectly))
            false
        } else {
            true
        }
    }


    fun createAuthUser() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.value ?:return, password.value ?:"")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseAuth.getInstance().currentUser?.uid?.also {
                        createUser(it)
                        return@addOnCompleteListener
                    }

                } else {
                    Toast.makeText(applicationContext, "FAILED1", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun createUser(uid: String) {
        val user = User().apply {
            userId = uid
            name = nameInput.value ?:""
        }
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .set(user)
            .addOnCompleteListener { task->
                if (task.isSuccessful) {
                    myUser = user
                    userCreated.postValue(true)
                } else {
                    Toast.makeText(applicationContext, "FAILED2", Toast.LENGTH_SHORT).show()
                }
            }
    }

}