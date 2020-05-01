package com.chirag.taskassinment.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chirag.taskassinment.R
import com.chirag.taskassinment.data.model.ErrorResponse
import com.chirag.taskassinment.data.model.Result
import com.chirag.taskassinment.data.model.Token
import com.chirag.taskassinment.databinding.ActivityLoginBinding
import com.chirag.taskassinment.ui.status.StatusActivity

class LoginActivity : AppCompatActivity() {

    lateinit var loginViewBinding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        /* val username = findViewById<EditText>(R.id.username)
         val password = findViewById<EditText>(R.id.password)
         val login = findViewById<Button>(R.id.login)
         val loading = findViewById<ProgressBar>(R.id.loading)*/

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)
        loginViewBinding.lifecycleOwner = this

        loginViewBinding.loginViewModel = loginViewModel

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            loginViewBinding.login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                loginViewBinding.username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                loginViewBinding.password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
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
            loginViewBinding.loading.visibility = View.INVISIBLE
        })

        loginViewBinding.username.afterTextChanged {
            /* loginViewModel.loginDataChanged(
                 loginViewBinding.username.text.toString(),
                 loginViewBinding.password.text.toString()
             )*/
            loginViewModel.loginDataChanged()
        }

        loginViewBinding.password.apply {
            afterTextChanged {
                /*loginViewModel.loginDataChanged(
                    loginViewBinding.username.text.toString(),
                    loginViewBinding.password.text.toString()
                )*/
                loginViewModel.loginDataChanged()
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        /* loginViewModel.login(
                             loginViewBinding.username.text.toString(),
                             loginViewBinding.password.text.toString()
                         )*/
                        loginViewModel.login()
                }
                false
            }

        }
    }

    fun onLoginBtnClicked(view: View) {
        loginViewModel.login().observe(this@LoginActivity, Observer {
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
            loginViewBinding.loading.visibility = View.INVISIBLE
        })
    }

    /*fun onLoginBtnClicked(view: View) {
        loginViewBinding.loading.visibility = View.VISIBLE
//        loginViewModel.login(loginViewBinding.username.text.toString(), loginViewBinding.password.text.toString())
        loginViewModel.login()
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
                    loginViewBinding.loading.visibility = View.INVISIBLE
                })
    }*/

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
