package com.punksta.apps.robopoetry.model

import com.punksta.apps.robopoetry.entity.*
import io.reactivex.Single

/**
 * Created by stanislav on 1/1/17.
 */
interface DataModel {
    fun getPoem(writerId: String, poemId: String) : Single<Poem>
    fun queryWriters(queryString: String? = null, order: Order = Order.ASK) : Single<List<WriterInfo>>
    fun queryPoems(writerId: String? = null, query: String? = null, cutLimit: Int? = 100) : Single<List<Poem>>
    fun getCelebration(celebration: Celebration) : Single<List<CelebrationItem>>
    fun onLawMemory()

    fun getGreetingForRobot(robot: Robot) : String

    fun getRobots() : List<Robot>
    fun getCurrent() : Robot
    fun setCurrent(robot: Robot)
}