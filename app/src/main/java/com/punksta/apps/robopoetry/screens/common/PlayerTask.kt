package com.punksta.apps.robopoetry.screens.common

import com.punksta.apps.robopoetry.entity.Celebration
import com.punksta.apps.robopoetry.entity.CelebrationItem
import com.punksta.apps.robopoetry.entity.Poem
import com.punksta.apps.robopoetry.entity.WriterInfo

/**
 * Created by stanislav on 3/8/17.
 */
sealed class PlayerTask(val voice: String)


class CelebrationTask(voice: String,
                  val celebrationItem: CelebrationItem,
                  val userName: String,
                  val celebration: Celebration) : PlayerTask(voice)

class PoemTask(voice: String,
               val poem: Poem,
               val writer: WriterInfo) : PlayerTask(voice)


class GreetingTask(voice: String,
                   val name: String,
                   val greeting: String) : PlayerTask(voice)