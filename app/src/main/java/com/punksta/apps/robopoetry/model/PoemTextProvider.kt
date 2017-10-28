package com.punksta.apps.robopoetry.model

/**
 * Created by stanislav on 3/9/17.
 */
interface PoemTextProvider {
    fun getPoemText(writerId: String, poemId: String) : String
}