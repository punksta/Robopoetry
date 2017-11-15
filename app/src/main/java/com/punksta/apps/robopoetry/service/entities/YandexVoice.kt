package com.punksta.apps.robopoetry.service.entities

import ru.yandex.speechkit.Vocalizer

/**
 * Created by stanislav on 11/11/17.
 */
enum class YandexVoice(val yandexVoiceId: String) {
    ALYSS(Vocalizer.Voice.ALYSS),
    ERMIL(Vocalizer.Voice.ERMIL),
    JANE(Vocalizer.Voice.JANE),
    OMAZH(Vocalizer.Voice.OMAZH),
    ZAHAR(Vocalizer.Voice.ZAHAR)
}