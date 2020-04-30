package com.chirag.taskassinment.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chirag.taskassinment.R
import com.chirag.taskassinment.data.model.ErrorResponse
import com.chirag.taskassinment.data.model.Result
import com.chirag.taskassinment.data.model.Token
import com.chirag.taskassinment.ui.status.StatusActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
                    .observe(this@LoginActivity,
                        Observer {
                            val loginResult = it ?: return@Observer
                            if (loginResult is Result.Success) {
                                val token: Token = loginResult.data as Token
                                Log.d("TAG", "Success Response token is ${token.token}")
                                updateUiWithUser(token.token)
                            } else if (loginResult is Result.Error) {
                                val errorResult = loginResult.errorResponse as ErrorResponse
                                Log.d("TAG", "Error is ${errorResult.error}")
                                Log.d("TAG", "Error Description is ${errorResult.description}")
                                showLoginFailed(errorResult.description)
                            } else if (loginResult is Result.Failure) {
                                // val failure = loginResult.exception as Result.Failure
                                loginResult.exception.message?.let { it1 -> showLoginFailed(it1) }
                            }
                            loading.visibility = View.INVISIBLE
                        })
            }
        }
    }

    private fun updateUiWithUser(token: String) {
        Toast.makeText(
            applicationContext,
            "LoggedIn with token $token",
            Toast.LENGTH_SHORT
        ).show()
        startActivity(
            Intent(
                this@LoginActivity,
                StatusActivity::class.java
            )
        )
    }

    private fun showLoginFailed(errorString: String) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
