package com.chirag.taskassinment.ui.login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.chirag.taskassinment.R
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by Chirag Sidhiwala on 2/5/20.
 */
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    var mLoginActivityRule = ActivityTestRule(LoginActivity::class.java)


    @Test
    fun clickLoginButton_withIncorrectUserName() {
        val username = "test"
        val password = "password"

        onView(withId(R.id.username)).perform(ViewActions.typeText(username))
        onView(withId(R.id.password)).perform(ViewActions.typeText(password))

        onView(withId(R.id.username)).check(
            ViewAssertions.matches(
                hasErrorText("Not a valid username")
            )
        );

        onView(withId(R.id.login)).check(
            ViewAssertions.matches(
                Matchers.not(
                    isEnabled()
                )
            )
        )
    }


    @Test
    fun clickLoginButton_withIncorrectPassword() {
        val username = "test@gmail.com"
        val password = "password"

        onView(withId(R.id.username)).perform(ViewActions.typeText(username))
        onView(withId(R.id.password)).perform(ViewActions.typeText(password))

        onView(withId(R.id.password)).check(
            ViewAssertions.matches(
                hasErrorText("Password should be of 8–15 characters length including at least 1 uppercase, 1 lowercase, 1 number and 1 special character")
            )
        );

        onView(withId(R.id.login)).check(
            ViewAssertions.matches(
                Matchers.not(
                    isEnabled()
                )
            )
        )
    }

    @Test
    fun clickLoginButton_withCorrectPattern() {
        val username = "test@gmail.com"
        val password = "Password@2020"

        onView(withId(R.id.username)).perform(ViewActions.typeText(username))
        onView(withId(R.id.password)).perform(ViewActions.typeText(password))

        onView(withId(R.id.login)).check(
            ViewAssertions.matches(
                isEnabled()
            )
        )
        onView(withId(R.id.login)).perform(ViewActions.click())

        onView(withText("Email address and password is not a valid combination.")).inRoot(
            RootMatchers.withDecorView(
                Matchers.not(mLoginActivityRule.activity.window.decorView)
            )
        ).check(
            ViewAssertions.matches(isDisplayed())
        )

    }


    @Test
    fun clickLoginButton_withCorrectCredential() {
        val username = "test@worldofplay.in"
        val password = "Worldofplay@2020"

        onView(withId(R.id.username)).perform(ViewActions.typeText(username))
        onView(withId(R.id.password)).perform(ViewActions.typeText(password))

        onView(withId(R.id.login)).check(
            ViewAssertions.matches(
                isEnabled()
            )
        )
        onView(withId(R.id.login)).perform(ViewActions.click())

        onView(withId(R.id.constraintContainer))
            .check(ViewAssertions.matches(isDisplayed()))

        onView(withId(R.id.linearProgressIndicatorLay))
            .check(ViewAssertions.matches(isDisplayed()))

        onView(withId(R.id.currentlyDisplayedView))
            .check(ViewAssertions.matches(isDisplayed()))

        onView(withId(R.id.descriptionTv))
            .check(ViewAssertions.matches(isDisplayed()))

    }

}