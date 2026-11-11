package com.shihcheeng.sspaiyou.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class SSPaiUrlHandlerTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext

    @Test
    fun webLinksUseCallbackAndNormalizeSchemeWithoutLosingQueryOrFragment() {
        val context = RecordingContext(appContext)
        val opened = mutableListOf<String>()
        val handler = SSPaiUrlHandler(context, urlInnerOpen = true, onInnerOpen = { opened += it })

        handler.openUri("HTTPS://sspai.com/post/123?source=reader#comments")
        handler.openUri("http://example.com/")

        assertEquals(listOf("https://sspai.com/post/123?source=reader#comments", "http://example.com/"), opened)
        assertTrue(context.intents.isEmpty())
    }

    @Test
    fun externalWebLinksFromApplicationContextStartANewTask() {
        val context = RecordingContext(appContext)
        val handler = SSPaiUrlHandler(context, urlInnerOpen = false) { error("Unexpected callback") }

        handler.openUri("HTTPS://sspai.com/")

        val intent = context.intents.single()
        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals("https://sspai.com/", intent.dataString)
        assertTrue(intent.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0)
    }

    @Test
    fun nonWebLinksUseSystemEvenWhenInternalBrowserIsEnabled() {
        val context = RecordingContext(appContext)
        val handler = SSPaiUrlHandler(context, urlInnerOpen = true) { error("Unexpected callback") }
        val links = listOf("mailto:hello@example.com", "tel:12345", "sspai://post/123")

        links.forEach(handler::openUri)

        assertEquals(links, context.intents.map { it.dataString })
        assertTrue(context.intents.all { it.action == Intent.ACTION_VIEW })
    }

    @Test
    fun wrappedActivityKeepsItsExistingTask() {
        lateinit var activity: Activity
        InstrumentationRegistry.getInstrumentation().runOnMainSync { activity = Activity() }
        val context = RecordingContext(ContextWrapper(activity))
        val handler = SSPaiUrlHandler(context, urlInnerOpen = false) { error("Unexpected callback") }

        handler.openUri("https://sspai.com/")

        assertEquals(0, context.intents.single().flags and Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    @Test
    fun missingExternalHandlerIsWrappedButCallbackErrorsArePreserved() {
        val missingActivity = ActivityNotFoundException("No handler")
        val context = RecordingContext(appContext, missingActivity)
        val external = SSPaiUrlHandler(context, urlInnerOpen = false) { error("Unexpected callback") }

        val wrapped = assertThrows(IllegalArgumentException::class.java) {
            external.openUri("https://sspai.com/")
        }
        assertSame(missingActivity, wrapped.cause)

        val internal = SSPaiUrlHandler(context, urlInnerOpen = true) { throw missingActivity }
        val original = assertThrows(ActivityNotFoundException::class.java) {
            internal.openUri("https://sspai.com/")
        }
        assertSame(missingActivity, original)
    }

    @Test
    fun missingSchemeOrWebHostIsRejectedBeforeDispatch() {
        val context = RecordingContext(appContext)
        val invalidLinks = listOf("", "sspai.com/post/123", "/post/123", "https:///post/123", "https:")

        for (internal in listOf(true, false)) {
            val handler = SSPaiUrlHandler(context, internal) { error("Unexpected callback") }
            for (link in invalidLinks) {
                assertThrows(IllegalArgumentException::class.java) { handler.openUri(link) }
            }
        }
        assertTrue(context.intents.isEmpty())
    }

    private class RecordingContext(
        base: Context,
        private val failure: ActivityNotFoundException? = null
    ) : ContextWrapper(base) {
        val intents = mutableListOf<Intent>()

        override fun startActivity(intent: Intent) {
            failure?.let { throw it }
            intents += intent
        }
    }
}
