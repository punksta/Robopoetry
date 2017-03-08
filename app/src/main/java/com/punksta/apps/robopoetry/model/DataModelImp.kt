package com.punksta.apps.robopoetry.model

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodSubtype
import com.crashlytics.android.Crashlytics
import com.fasterxml.jackson.core.*
import com.opencsv.CSVReader
import com.punksta.apps.robopoetry.R
import com.punksta.apps.robopoetry.entity.Poem
import com.punksta.apps.robopoetry.entity.Order
import com.punksta.apps.robopoetry.entity.WriterInfo
import io.fabric.sdk.android.Fabric
import io.fabric.sdk.android.Logger
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.yandex.speechkit.Vocalizer
import java.io.InputStreamReader
import java.util.*


/**
 * Created by stanislav on 1/2/17.
 */

class DataModelImp(context: Context) : DataModel {


    private val app = context.applicationContext
    private val assetsManager: AssetManager
        get() = app.assets

    private val contentPath = "content.csv"
    private val poemsPath = "poems"


    private val jsonFactory = JsonFactory()
    private var writerCache: List<WriterInfo>? = null


    private val translate : Itranliteration = NaiveTransliteration()

    private inline fun keyBoardgetLocal() : Locale {
        val imm = app.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val ims = imm.currentInputMethodSubtype
        val locale =  ims.locale
        return Locale(locale)
    }

    private fun transform(query: String?): String? {
        return when {
            query == null -> null
            keyBoardgetLocal().displayLanguage == "русский" -> query
            else -> {
                Crashlytics.log(0, "transform", query)
                translate.latToCyr(query)
            }
        }
    }


    private fun readWritersFromAssets(assetManager: AssetManager, path: String): List<WriterInfo> {
        val result = mutableListOf<WriterInfo>()

        assetManager.open(path)
                .let(::InputStreamReader)
                .let { CSVReader(it, ',') }
                .use {
                    while (true) {
                        val line: Array<String>? = it.readNext()
                        if (line?.isEmpty() ?: true) {
                            break
                        } else {
                            result += WriterInfo(line!![0], line[1], line[2].toInt())
                        }
                    }
                }

        return result
    }


    private fun getWriters() = if (writerCache == null) {
        Single.fromCallable { readWritersFromAssets(assetsManager, contentPath) }
                .doOnSuccess {
                    writerCache = it
                }
    } else {
        Single.just(writerCache!!)
    }



    override fun queryWriters(queryString: String?, order: Order): Single<List<WriterInfo>> {
        val q = transform(queryString)
        Log.v("query", "queryWriters: ${q}")

        return getWriters()
                .map {
                    if (q != null) {
                        it.filter { it queryPredicate q }
                    } else {
                        it
                    }
                }
                .map {
                    if (order == Order.ASK) {
                        it.sortedBy { it.name }
                    } else {
                        it.sortedByDescending { it.name }
                    }
                }
    }


    override fun getPoem(writerId: String, poemId: String): Single<Poem> {
        return Single.fromCallable {
            assetsManager.open("$poemsPath/$writerId.poem.json")
                    .let(::InputStreamReader)
                    .use {
                        val parser = jsonFactory.createParser(it)
                        parser.parsePoems(idList = listOf(poemId)).first()
                    }
        }
    }


    private fun loadFastPoems(writerId: String, cutSize: Int?, query: String?): List<Poem> {
        return assetsManager.open("$poemsPath/$writerId.poem.json")
                .let(::InputStreamReader)
                .use {
                    val q = transform(query)
                    Log.v("query", "loadFastPoems: ${q}")

                    val parser = jsonFactory.createParser(it)
                    parser.parsePoems(cutSize, q)
                }
    }


    override fun queryPoems(writerId: String?, query: String?, cutSize: Int?): Single<List<Poem>> {
        val writers: Single<List<String>> = if (writerId != null) Single.just(listOf(writerId)) else getWriters().map { it.map { it.id } }

        val q = transform(query)
        Log.v("query", "queryPoems: ${q}")

        return writers.toObservable()
                .flatMap { Observable.fromIterable(it) }
                .map { loadFastPoems(it, cutSize, q) }
                .reduce(listOf<Poem>(), { list1, list2 -> list1 + list2 })
    }


    override fun onLawMemory() {
        writerCache = null
    }


    private val r = listOf(
            Robot(R.drawable.face1, R.string.face1, Voice.YandexVoice(Vocalizer.Voice.ALYSS)),
            Robot(R.drawable.face2, R.string.face2, Voice.YandexVoice(Vocalizer.Voice.ERMIL)),
            Robot(R.drawable.face3, R.string.face3, Voice.YandexVoice(Vocalizer.Voice.JANE)),
            Robot(R.drawable.face4, R.string.face4, Voice.YandexVoice(Vocalizer.Voice.OMAZH)),
            Robot(R.drawable.face5, R.string.face5, Voice.YandexVoice(Vocalizer.Voice.ZAHAR))
    )

    private var currentRobot: Robot? = null


    override fun getRobots(): List<Robot> = r


    override fun getCurrent(): Robot {
        if (currentRobot == null) {
            currentRobot = r.first()
        }
        return currentRobot!!
    }

    override fun setCurrent(robot: Robot) {
        currentRobot = robot
    }
}


private fun JsonParser.parsePoem(): Poem {
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



private infix fun WriterInfo.queryPredicate(query: String?): Boolean {
    return when(query) {
        null -> true
        else -> name.split(" ").any { it.startsWith(query, true) }
    }
}

private infix fun Poem.queryPredicate(query: String?) : Boolean {
    return when(query) {
        null -> true
        else -> name.contains(query, true)  || сutText.contains(query, true)
    }
}


private tailrec fun JsonParser.parsePoemsInner(result: List<Poem>, cutSize: Int?, predicate: (Poem) -> Boolean): List<Poem> {
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


private fun JsonParser.parsePoems(cutSize: Int? = null, query: String? = null, idList: List<String>? = null): List<Poem> {
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

private fun String.cutString(begin: Int, end: Int): String {
    return if (length < end)
        this
    else
        substring(begin, end)

}


