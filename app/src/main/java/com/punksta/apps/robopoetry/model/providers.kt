package com.punksta.apps.robopoetry.model

import android.content.Context
import android.content.res.AssetManager
import com.fasterxml.jackson.core.JsonFactory
import com.punksta.apps.robopoetry.entity.GreetingsSpeechTask
import com.punksta.apps.robopoetry.entity.PoemSpeechTask
import org.funktionale.memoization.memoize
import java.io.InputStreamReader

/**
 * Created by stanislav on 11/16/17.
 */
class GreetingsSpeechTaskProvider : TaskTextProviderT<GreetingsSpeechTask> {
    override fun provide(t: GreetingsSpeechTask): String {
        return t.text
    }
}

class PoemReadProvider(val context: Context) : TaskTextProviderT<PoemSpeechTask> {
    private val assetsManager: AssetManager
        get() = context.assets

    private val poemsPath = "poems"
    private val jsonFactory = JsonFactory()

    private fun readTextFromDisk(writerId: String, poemId: String): String {
        return assetsManager.open("$poemsPath/$writerId.poem.json")
                .let(::InputStreamReader)
                .use {
                    val parser = jsonFactory.createParser(it)
                    parser.parsePoems(idList = listOf(poemId))
                            .first()
                            .сutText
                }
    }

    private val readTextFromDiskMemoized = this::readTextFromDisk.memoize()

    override fun provide(t: PoemSpeechTask): String {
        return t.title + " " + readTextFromDiskMemoized(t.writerId, t.poemId) + ". \n\t";
    }
}