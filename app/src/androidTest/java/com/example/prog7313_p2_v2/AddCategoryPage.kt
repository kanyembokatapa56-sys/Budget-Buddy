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
class AddCategoryPageTest {

    @Before
    fun setUp() {
        Intents.init()
        ActivityScenario.launch(Add_Category_Page::class.java)
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testBackButtonNavigatesToEditCategoriesPage() {
        onView(withId(R.id.ibExit)).perform(click())
        Intents.intended(hasComponent(Edit_Categories_Page::class.java.name))
    }

    @Test
    fun testCreateCategoryButtonNavigatesToEditCategoriesPage() {
        onView(withId(R.id.btnCreateCategory)).perform(click())
        Intents.intended(hasComponent(Edit_Categories_Page::class.java.name))
    }
}
