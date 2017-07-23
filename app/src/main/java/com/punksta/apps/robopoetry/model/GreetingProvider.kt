package com.punksta.apps.robopoetry.model

import android.content.Context
import android.content.res.Resources
import android.support.annotation.ArrayRes
import com.punksta.apps.robopoetry.R
import java.security.SecureRandom
import java.util.*

/**
 * Created by stanislav on 7/23/17.
 */


typealias ArrayIndexed = Pair<List<String>, Int>

val ArrayIndexed.isEnded
    get() = this.first.size == this.second + 1


object GreetingProvider {
    val mapping: Map<RobotEnum, Int> = mapOf(
            RobotEnum.Hana to R.array.hanna_greetings,
            RobotEnum.Riko to R.array.ricko_greetings,
            RobotEnum.Tomiko to R.array.tomiko_greetings,
            RobotEnum.Taro to R.array.tara_greetings,
            RobotEnum.ChiyoChiyo to R.array.chio_greetings
    )

    fun getGreetingForRobot(context: Context, robot: RobotEnum) =
            getRandomOfArray(context, mapping[robot]!!)

    private val random by lazy { SecureRandom() }


    private val cache = mutableMapOf<Int, ArrayIndexed>()
    fun getRandomOfArray(context: Context, @ArrayRes id: Int): String {
        var array = getCachedOrCreate(context, id)

        if (array.isEnded) {
            cache.remove(id)
            array = getCachedOrCreate(context, id)
        }

        val result = array.first[array.second]

        cache[id] = array.first to  array.second + 1

        return result
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