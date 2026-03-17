package com.example.prog7313_p2_v2

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SignUpPageTest {

    @Before
    fun setUp() {
        Intents.init()
        ActivityScenario.launch(SignUp_Page::class.java)
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testTermsConditionsNavigation() {
        onView(withId(R.id.tvTermsConditions)).perform(click())
        Intents.intended(hasComponent(Terms_And_Conditions_Page::class.java.name))
    }

    @Test
    fun testPrivacyPolicyNavigation() {
        onView(withId(R.id.tvPrivacyPolicy)).perform(click())
        Intents.intended(hasComponent(Privacy_Policy_Page::class.java.name))
    }

    @Test
    fun testLoginNavigation() {
        onView(withId(R.id.btnLogin)).perform(click())
        Intents.intended(hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun testSignUpWithEmptyFieldsShowsError() {
        onView(withId(R.id.btnSignUp)).perform(click())

        // Check that error messages are displayed for empty inputs
        onView(withId(R.id.etFullName)).check { view, noViewFoundException ->
            assert((view as? android.widget.EditText)?.error != null)
        }
        onView(withId(R.id.etPhoneNumber)).check { view, _ ->
            assert((view as? android.widget.EditText)?.error != null)
        }
        onView(withId(R.id.etEmail)).check { view, _ ->
            assert((view as? android.widget.EditText)?.error != null)
        }
        onView(withId(R.id.etPassword)).check { view, _ ->
            assert((view as? android.widget.EditText)?.error != null)
        }
    }
}