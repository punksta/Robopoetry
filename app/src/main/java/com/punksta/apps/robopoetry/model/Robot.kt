package com.punksta.apps.robopoetry.model

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.punksta.apps.robopoetry.R
import ru.yandex.speechkit.Vocalizer

/**
 * Created by stanislav on 1/6/17.
 */

sealed class Voice {
    class YandexVoice(val voice: String) : Voice()
}

data class Robot(@DrawableRes val drawableId: Int,
                 @StringRes val nameId: Int,
                 val voice: Voice)


enum class RobotEnum(val robot: com.punksta.apps.robopoetry.model.Robot) {
    Riko(Robot(R.drawable.face1, R.string.face1, Voice.YandexVoice(Vocalizer.Voice.ALYSS))),
    ChiyoChiyo(Robot(R.drawable.face2, R.string.face2, Voice.YandexVoice(Vocalizer.Voice.ERMIL))),
    Hana(Robot(R.drawable.face3, R.string.face3, Voice.YandexVoice(Vocalizer.Voice.JANE))),
    Tomiko(Robot(R.drawable.face4, R.string.face4, Voice.YandexVoice(Vocalizer.Voice.OMAZH))),
    Taro(Robot(R.drawable.face5, R.string.face5, Voice.YandexVoice(Vocalizer.Voice.ZAHAR)))
}

fun Robot.toEnum() : RobotEnum = RobotEnum.values().first { it.robot == this }