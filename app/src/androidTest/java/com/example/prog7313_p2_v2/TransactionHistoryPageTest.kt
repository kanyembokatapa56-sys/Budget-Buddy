package com.example.prog7313_p2_v2

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TransactionHistoryPageTest {

    @Before
    fun setUp() {
        Intents.init()
        ActivityScenario.launch(Transaction_History_Page::class.java)
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testHomeButtonLaunchesHomePage() {
        onView(withId(R.id.ibHome)).perform(click())
        Intents.intended(hasComponent(Home_Page::class.java.name))
    }

    @Test
    fun testGraphButtonLaunchesGraphPage() {
        onView(withId(R.id.ibGraphs)).perform(click())
        Intents.intended(hasComponent(Graph_Page::class.java.name))
    }

    @Test
    fun testSettingsButtonLaunchesSettingsPage() {
        onView(withId(R.id.ibSettings)).perform(click())
        Intents.intended(hasComponent(Budget_Settings_Page::class.java.name))
    }

    @Test
    fun testProfileButtonLaunchesProfilePage() {
        onView(withId(R.id.ibProfile)).perform(click())
        Intents.intended(hasComponent(ProfilePageActivity::class.java.name))
    }
}