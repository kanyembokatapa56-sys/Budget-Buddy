package com.example.prog7313_p2_v2

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @Before
    fun setUp() {
        Intents.init()
        ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testSignUpButtonNavigatesToSignUpPage() {
        onView(withId(R.id.btnSignup)).perform(click())
        Intents.intended(hasComponent(SignUp_Page::class.java.name))
    }

    @Test
    fun testEmptyEmailAndPasswordShowsErrors() {
        onView(withId(R.id.btnLogin)).perform(click())

        onView(withId(R.id.etEmail)).check { view, _ ->
            assert((view as? android.widget.EditText)?.error != null)
        }

        onView(withId(R.id.etPassword)).check { view, _ ->
            assert((view as? android.widget.EditText)?.error != null)
        }
    }

    @Test
    fun testEmailFieldErrorOnlyWhenPasswordIsEntered() {
        onView(withId(R.id.etPassword)).perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.btnLogin)).perform(click())

        onView(withId(R.id.etEmail)).check { view, _ ->
            assert((view as? android.widget.EditText)?.error != null)
        }
    }

    @Test
    fun testPasswordFieldErrorOnlyWhenEmailIsEntered() {
        onView(withId(R.id.etEmail)).perform(typeText("user@example.com"), closeSoftKeyboard())
        onView(withId(R.id.btnLogin)).perform(click())

        onView(withId(R.id.etPassword)).check { view, _ ->
            assert((view as? android.widget.EditText)?.error != null)
        }
    }
}