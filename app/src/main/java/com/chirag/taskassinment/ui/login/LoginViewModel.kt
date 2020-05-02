package com.chirag.taskassinment.ui.login

import android.text.TextUtils
import android.util.Patterns
import androidx.core.util.PatternsCompat
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chirag.taskassinment.R
import com.chirag.taskassinment.data.LoginRepository
import com.chirag.taskassinment.data.model.Result
import com.chirag.taskassinment.utility.PASSWORD_PATTERN
import java.util.regex.Pattern

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    var userName = ObservableField<String>()

    var userPassWord = ObservableField<String>()

     fun login(): LiveData<Result<Any>> {
         // can be launched in a separate asynchronous job
         // val loginInfoValue = loginInfo.value
         return loginRepository.login(userName.get().orEmpty(), userPassWord.get().orEmpty())
     }


    fun loginDataChanged() {
        if (!isUserNameValid(userName.get()!!.trim())) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!TextUtils.isEmpty(userPassWord.get()) && !isPasswordValid(userPassWord.get()!!.trim())) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            PatternsCompat.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        val pattern = Pattern.compile(PASSWORD_PATTERN)
        if (password.isNotBlank())
            return pattern.matcher(password).matches()

        return password.isBlank()
    }

}
