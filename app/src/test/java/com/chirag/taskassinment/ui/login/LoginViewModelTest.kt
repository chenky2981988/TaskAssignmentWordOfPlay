package com.chirag.taskassinment.ui.login

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.chirag.taskassinment.R
import com.chirag.taskassinment.data.LoginRepository
import com.chirag.taskassinment.data.model.ErrorResponse
import com.chirag.taskassinment.data.model.Result
import com.chirag.taskassinment.data.model.Token
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.*

/**
 * Created by Chirag Sidhiwala on 2/5/20.
 */
@RunWith(JUnit4::class)
class LoginViewModelTest {

    @Mock
    private lateinit var context: Application

    @Mock
    private lateinit var loginRepository: LoginRepository

    private lateinit var loginViewModel: LoginViewModel

    private var loginMutableLiveData: MutableLiveData<Result<Any>> = MutableLiveData()
    private lateinit var loginFormObserver: Observer<LoginFormState>

    @get:Rule
    val rule = InstantTaskExecutorRule()


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`<Context>(context.applicationContext).thenReturn(context)

        loginViewModel = LoginViewModel(this.loginRepository)
        mockObserver()
    }

    private fun mockObserver() {
        loginFormObserver = Mockito.mock(Observer::class.java) as Observer<LoginFormState>
    }

    @Test
    fun loginWithSuccess() {

        val userName = "test@gmail.in"
        val password = "Worldofplay@2020"

        val token = Token("VwvYXBpXC9")

        loginMutableLiveData.value = Result.Success(token)
        Mockito.`when`(
            this.loginRepository.login(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
            )
        ).thenAnswer {
            return@thenAnswer loginMutableLiveData
        }

        loginViewModel.userName.set(userName)
        loginViewModel.userPassWord.set(password)
        loginViewModel.login().observeForever {
            assertTrue("Test Success", (it is Result.Success))
            val successResult = it as Result.Success
            assertTrue("Verify success response object", successResult.data is Token)
            val successResponseObj = successResult.data as Token
            assertTrue("verify token", successResponseObj.token == "VwvYXBpXC9")
        }
    }


    @Test
    fun loginWithInvalidUserName() {
        val userName = "test@gmail.in"
        val password = "Worldofplay@2020"

        val errorResponse = ErrorResponse(
            "invalid_credentials",
            "Email address and password is not a valid combination."
        )

        loginMutableLiveData.value = Result.Error(errorResponse)
        Mockito.`when`(
            this.loginRepository.login(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
            )
        ).thenAnswer {
            return@thenAnswer loginMutableLiveData
        }

        loginViewModel.userName.set(userName)
        loginViewModel.userPassWord.set(password)
        loginViewModel.login().observeForever {
            assertTrue("Test invalid username", (it is Result.Error))
            val errorResult = it as Result.Error
            assertTrue("Verify error response object", errorResult.errorResponse is ErrorResponse)
            val errorResponseObj = errorResult.errorResponse as ErrorResponse
            assertTrue("error check", errorResponseObj.error == "invalid_credentials")
            assertTrue(
                "description check",
                errorResponseObj.description == "Email address and password is not a valid combination."
            )
        }
    }

    @Test
    fun loginWithInvalidPassword() {
        val userName = "test@worldofplay.in"
        val password = "Test@1234"

        val errorResponse = ErrorResponse(
            "invalid_credentials",
            "Email address and password is not a valid combination."
        )

        loginMutableLiveData.value = Result.Error(errorResponse)
        Mockito.`when`(
            this.loginRepository.login(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
            )
        ).thenAnswer {
            return@thenAnswer loginMutableLiveData
        }

        loginViewModel.userName.set(userName)
        loginViewModel.userPassWord.set(password)
        loginViewModel.login().observeForever {
            assertTrue("Test invalid password", (it is Result.Error))
            val errorResult = it as Result.Error
            assertTrue("Verify error response object", errorResult.errorResponse is ErrorResponse)
            val errorResponseObj = errorResult.errorResponse as ErrorResponse
            assertTrue("error check", errorResponseObj.error == "invalid_credentials")
            assertTrue(
                "description check",
                errorResponseObj.description == "Email address and password is not a valid combination."
            )
        }
    }

    @Test
    fun loginWithInvalidCredential() {
        val userName = "chirag@gmail.com"
        val password = "Test@1234"

        val errorResponse = ErrorResponse(
            "invalid_credentials",
            "Email address and password is not a valid combination."
        )

        loginMutableLiveData.value = Result.Error(errorResponse)
        Mockito.`when`(
            this.loginRepository.login(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
            )
        ).thenAnswer {
            return@thenAnswer loginMutableLiveData
        }

        loginViewModel.userName.set(userName)
        loginViewModel.userPassWord.set(password)
        loginViewModel.login().observeForever {
            assertTrue("Test invalid password", (it is Result.Error))
            val errorResult = it as Result.Error
            assertTrue("Verify error response object", errorResult.errorResponse is ErrorResponse)
            val errorResponseObj = errorResult.errorResponse as ErrorResponse
            assertTrue("error check", errorResponseObj.error == "invalid_credentials")
            assertTrue(
                "description check",
                errorResponseObj.description == "Email address and password is not a valid combination."
            )
        }
    }

    @Test
    fun loginDataChanged_success() {
        val username = "test@gmail.in"
        val password = "Worldofplay@2020"
        val isDataValid = true

        val successLoginForm = LoginFormState(isDataValid = isDataValid)
        val usernameErrorLoginForm = LoginFormState(usernameError = R.string.invalid_username)
        val passwordErrorLoginForm = LoginFormState(isDataValid = isDataValid)

        with(loginViewModel) {
            userName.set(username)
            userPassWord.set(password)
            loginDataChanged()
            loginFormState.observeForever(loginFormObserver)
        }

        assertNotNull(loginViewModel.loginFormState.value)
        val loginFormStateObj = loginViewModel.loginFormState.value as LoginFormState
        assertNull("password null", loginFormStateObj.passwordError)
        assertNull("verify username error", loginFormStateObj.usernameError)
        assertTrue("is data valid true", loginFormStateObj == successLoginForm)

    }

    @Test
    fun loginDataChanged_inCorrectUsername() {
        val username = "test"
        val password = "Worldofplay@2020"

        val usernameErrorLoginForm = LoginFormState(usernameError = R.string.invalid_username)

        with(loginViewModel) {
            userName.set(username)
            userPassWord.set(password)
            loginDataChanged()
            loginFormState.observeForever(loginFormObserver)
        }

        assertNotNull(loginViewModel.loginFormState.value)
        val loginFormStateObj = loginViewModel.loginFormState.value as LoginFormState
        assertTrue("verify username error", loginFormStateObj == usernameErrorLoginForm)
        assertNull("password null", loginFormStateObj.passwordError)
        assertFalse("is data valid true", loginFormStateObj.isDataValid)
    }

    @Test
    fun loginDataChanged_inCorrectPassword() {
        val username = "test@worldofplay.in"
        val password = "Worldofplay2020"

        val passwordErrorLoginForm = LoginFormState(passwordError = R.string.invalid_password)

        with(loginViewModel) {
            userName.set(username)
            userPassWord.set(password)
            loginDataChanged()
            loginFormState.observeForever(loginFormObserver)
        }

        assertNotNull(loginViewModel.loginFormState.value)
        val loginFormStateObj = loginViewModel.loginFormState.value as LoginFormState
        assertNull("Username null", loginFormStateObj.usernameError)
        assertTrue("Verify password error", loginFormStateObj == passwordErrorLoginForm)
        assertFalse("is data valid true", loginFormStateObj.isDataValid)
    }
}