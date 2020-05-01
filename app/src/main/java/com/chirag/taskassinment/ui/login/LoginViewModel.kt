package com.chirag.taskassinment.ui.login

import android.util.Patterns
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chirag.taskassinment.R
import com.chirag.taskassinment.data.LoginRepository
import com.chirag.taskassinment.data.model.Result
import java.util.regex.Pattern

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    companion object {
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,16}$"
    }

    var userName = ObservableField<String>()

    var userPassWord = ObservableField<String>()


    private var _loginResult = MutableLiveData<Result<Any>>()
    var loginResult: LiveData<Result<Any>> = _loginResult

    fun login(): LiveData<Result<Any>>{
        // can be launched in a separate asynchronous job
        // val loginInfoValue = loginInfo.value
        return loginRepository.login(userName.get().orEmpty(), userPassWord.get().orEmpty())
    }

    fun loginDataChanged() {
        if (!isUserNameValid(userName.get().orEmpty())) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(userPassWord.get().orEmpty())) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        val pattern = Pattern.compile(PASSWORD_PATTERN)
        if (password.isNotBlank())
            return pattern.matcher(password).matches()

        return password.isNotBlank()
    }

    /*override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }*/
}
