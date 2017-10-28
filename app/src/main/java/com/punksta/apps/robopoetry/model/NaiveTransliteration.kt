package com.punksta.apps.robopoetry.model

import io.fabric.sdk.android.Fabric

/**
 * Created by stanislav on 1/10/17.
 */

class NaiveTransliteration : Itranliteration {
    override fun latToCyr(input: String): String {
        val s = input.toUpperCase()
        val sb = StringBuilder(s.length)
        var i = 0

        while (i < s.length) {// Идем по строке слева направо. В принципе, подходит для обработки потока
            var ch = s[i]
            if (ch == 'J') { // Префиксная нотация вначале
                i++ // преходим ко второму символу сочетания
                if (i < s.length) {
                    ch = s[i]
                    when (ch) {
                        'E' -> sb.append('Ё')
                        'S' -> sb.append('Щ')
                        'H' -> sb.append('Ь')
                        'U' -> sb.append('Ю')
                        'A' -> sb.append('Я')
                        else -> i++
                    }
                } else {
                    sb.append('Ж')
                    i++
                }
            } else if (i + 1 < s.length && s[i + 1] == 'H' && !(i + 2 < s.length && s[i + 2] == 'H')) {// Постфиксная нотация, требует информации о двух следующих символах. Для потока придется сделать обертку с очередью из трех символов.
                when (ch) {
                    'Z' -> sb.append('Ж')
                    'K' -> sb.append('Х')
                    'C' -> sb.append('Ч')
                    'S' -> sb.append('Ш')
                    'E' -> sb.append('Э')
                    'H' -> sb.append('Ъ')
                    'I' -> sb.append('Ы')
                }
                i++ // пропускаем постфикс
            } else {// одиночные символы
                when (ch) {
                    'A' -> sb.append('А')
                    'B' -> sb.append('Б')
                    'V' -> sb.append('В')
                    'G' -> sb.append('Г')
                    'D' -> sb.append('Д')
                    'E' -> sb.append('Е')
                    'Z' -> sb.append('З')
                    'I' -> sb.append('И')
                    'Y' -> sb.append('Й')
                    'K' -> sb.append('К')
                    'L' -> sb.append('Л')
                    'M' -> sb.append('М')
                    'N' -> sb.append('Н')
                    'O' -> sb.append('О')
                    'P' -> sb.append('П')
                    'R' -> sb.append('Р')
                    'S' -> sb.append('С')
                    'T' -> sb.append('Т')
                    'U' -> sb.append('У')
                    'F' -> sb.append('Ф')
                    'C' -> sb.append('Ц')
                    else -> sb.append(ch)
                }
            }
            i++
        }
        return sb.toString()
    }

}
