package com.punksta.apps.robopoetry.model

import android.content.Context
import android.content.res.AssetManager
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.punksta.apps.robopoetry.entity.Poem
import java.io.InputStreamReader

/**
 * Created by stanislav on 3/9/17.
 */
class PoemTextProviderImp(val context: Context): PoemTextProvider {
    private val assetsManager: AssetManager
        get() = context.assets

    private val contentPath = "content.csv"
    private val poemsPath = "poems"
    private val jsonFactory = JsonFactory()


    override fun getPoemText(writerId: String, poemId: String) : String {
        return assetsManager.open("$poemsPath/$writerId.poem.json")
                .let(::InputStreamReader)
                .use {
                    val parser = jsonFactory.createParser(it)
                    parser.parsePoems(idList = listOf(poemId))
                            .first()
                            .сutText
                }
    }
}



