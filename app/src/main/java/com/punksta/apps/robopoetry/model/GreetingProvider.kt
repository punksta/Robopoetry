package com.punksta.apps.robopoetry.model

import android.content.Context
import android.support.annotation.ArrayRes
import com.punksta.apps.robopoetry.R
import java.util.*

/**
 * Created by stanislav on 7/23/17.
 */


typealias ArrayIndexed = Pair<List<String>, Int>

val ArrayIndexed.currentIndex
    get() = this.second

val ArrayIndexed.array
    get() = this.first

val ArrayIndexed.isEnded
    get() = array.size == currentIndex + 1

val ArrayIndexed.nextArrayIndexed: ArrayIndexed
    get() = copy(second = currentIndex + 1)

object GreetingProvider {
    private val mapping: Map<RobotEnum, Int> = mapOf(
            RobotEnum.Hana to R.array.hanna_greetings,
            RobotEnum.Riko to R.array.ricko_greetings,
            RobotEnum.Tomiko to R.array.tomiko_greetings,
            RobotEnum.Taro to R.array.tara_greetings,
            RobotEnum.ChiyoChiyo to R.array.chio_greetings
    )

    fun getGreetingForRobot(context: Context, robot: RobotEnum) =
            getRandomOfArray(context, mapping[robot]!!)

    private val random by lazy { Random(System.currentTimeMillis()) }

    private val cache = mutableMapOf<Int, ArrayIndexed>()

    private fun getRandomOfArray(context: Context, @ArrayRes id: Int): String {
        var indexedArray = getCachedOrCreate(context, id)

        if (indexedArray.isEnded) {
            cache.remove(id)
            indexedArray = getCachedOrCreate(context, id)
        }

        val currentGreeting = indexedArray.first[indexedArray.currentIndex]

        updateCache(id, indexedArray.nextArrayIndexed)

        return currentGreeting
    }

    private fun updateCache(id: Int, newArrayIndexed: ArrayIndexed) {
        cache[id] = newArrayIndexed
    }

    private fun getCachedOrCreate(context: Context, @ArrayRes id: Int) : ArrayIndexed  {
        return if (cache.containsKey(id))
            cache[id]!!
        else {
            val list = context.resources.getStringArray(id).toMutableList();
            java.util.Collections.shuffle(list, random);
            return list to 0
        }
    }

}