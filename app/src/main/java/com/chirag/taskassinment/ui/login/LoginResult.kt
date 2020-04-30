package com.chirag.taskassinment.ui.login

import com.chirag.taskassinment.data.model.ErrorResponse
import com.chirag.taskassinment.data.model.Token

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
        val success: Token? = null,
        val error: ErrorResponse? = null
)
