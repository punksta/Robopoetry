package com.punksta.apps.robopoetry.model

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.punksta.apps.robopoetry.entity.Poem
import com.punksta.apps.robopoetry.entity.WriterInfo

/**
 * Created by stanislav on 3/9/17.
 */

fun JsonParser.parsePoem(): Poem {
    val id = currentName
    var poemText = ""
    var name = ""
    var year = ""
    var source = ""
    while (nextToken() != JsonToken.END_OBJECT) {
        when (currentName) {
            "name" -> {
                nextToken()
                name = text
            }

            "year" -> {
                nextToken()
                year = text
            }

            "source" -> {
                nextToken()
                source = text
            }

            "poem" -> {
                nextToken()
                poemText = text.trim()
            }
        }
    }
    return Poem(id, name, poemText, year, source)
}



infix fun WriterInfo.queryPredicate(query: String?): Boolean {
    return when(query) {
        null -> true
        else -> name.split(" ").any { it.startsWith(query, true) }
    }
}

infix fun Poem.queryPredicate(query: String?) : Boolean {
    return when(query) {
        null -> true
        else -> name.contains(query, true)  || сutText.contains(query, true)
    }
}


tailrec fun JsonParser.parsePoemsInner(result: List<Poem>, cutSize: Int?, predicate: (Poem) -> Boolean): List<Poem> {
    return if (nextToken() == JsonToken.END_OBJECT) {
        result
    } else {
        nextToken()
        val p: Poem = parsePoem()

        if (predicate(p)) {
            val cutter = if (cutSize != null) p.сutText.cutString(0, cutSize) else p.сutText
            parsePoemsInner(result + p.copy(сutText = cutter), cutSize, predicate)
        } else {
            parsePoemsInner(result, cutSize, predicate)
        }
    }
}


fun JsonParser.parsePoems(cutSize: Int? = null, query: String? = null, idList: List<String>? = null): List<Poem> {
    var result = listOf<Poem>()
    while (nextToken() != JsonToken.END_OBJECT) {
        val name = currentName
        when (name) {
            "id" -> nextToken()
            "name" -> nextToken()
            "poems" -> {
                nextToken()
                result = parsePoemsInner(emptyList(), cutSize) {
                    val idResult = idList?.contains(it.id) ?: true
                    if (idResult) {
                        val qResult = it queryPredicate query
                        qResult
                    } else {
                        false
                    }
                }
            }
        }
    }
    return result
}

fun String.cutString(begin: Int, end: Int): String {
    return if (length < end)
        this
    else
        substring(begin, end)

}


