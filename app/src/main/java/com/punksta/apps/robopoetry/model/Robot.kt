package com.punksta.apps.robopoetry.model

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.punksta.apps.robopoetry.R
import com.punksta.apps.robopoetry.service.entities.YandexVoice

/**
 * Created by stanislav on 1/6/17.
 */


data class Robot(@DrawableRes val drawableId: Int,
                 @StringRes val nameId: Int,
                 val voice: YandexVoice)


enum class RobotEnum(val robot: com.punksta.apps.robopoetry.model.Robot) {
    Riko(Robot(R.drawable.face1, R.string.face1, YandexVoice.ALYSS)),
    ChiyoChiyo(Robot(R.drawable.face2, R.string.face2, YandexVoice.ERMIL)),
    Hana(Robot(R.drawable.face3, R.string.face3, YandexVoice.JANE)),
    Tomiko(Robot(R.drawable.face4, R.string.face4, YandexVoice.OMAZH)),
    Taro(Robot(R.drawable.face5, R.string.face5, YandexVoice.ZAHAR))
}

fun Robot.toEnum() : RobotEnum = RobotEnum.values().first { it.robot == this }


fun YandexVoice.toRobotEnum(): RobotEnum = RobotEnum.values().first { it.robot.voice == this }