package com.punksta.apps.robopoetry.service.entities

import com.punksta.apps.robopoetry.service.SpeechTask
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import ru.yandex.speechkit.Error

sealed class SpeechEvent<out T : SpeechTask?>(val task: T) : PaperParcelable {
    @PaperParcel
    class OnProcessingStart(task: SpeechTask) : SpeechEvent<SpeechTask>(task) {
        companion object {
            @JvmField
            val CREATOR = PaperParcelSpeechEvent_OnProcessingStart.CREATOR
        }

        override fun toString(): String {
            return "OnProcessingStart(task=$task)"
        }
    }

    @PaperParcel
    class OnSpeechStart(task: SpeechTask) : SpeechEvent<SpeechTask>(task) {
        companion object {
            @JvmField
            val CREATOR = PaperParcelSpeechEvent_OnSpeechStart.CREATOR
        }

        override fun toString(): String {
            return "OnSpeechStart(task=$task)"
        }
    }

    @PaperParcel
    class OnSpeechEnd(task: SpeechTask) : SpeechEvent<SpeechTask>(task) {
        companion object {
            @JvmField
            val CREATOR = PaperParcelSpeechEvent_OnSpeechEnd.CREATOR
        }

        override fun toString(): String {
            return "OnSpeechEnd(task=$task)"
        }
    }

    @PaperParcel
    class OnSpeechError(task: SpeechTask, val error: Error) : SpeechEvent<SpeechTask>(task) {
        companion object {
            @JvmField
            val CREATOR = PaperParcelSpeechEvent_OnSpeechError.CREATOR
        }

        override fun toString(): String {
            return "OnSpeechError(task=$task)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as OnSpeechError

            if (error != other.error) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + error.hashCode()
            return result
        }
    }

    @PaperParcel
    class OnSpeechStopped(task: SpeechTask?) : SpeechEvent<SpeechTask?>(task) {
        companion object {
            @JvmField
            val CREATOR = PaperParcelSpeechEvent_OnSpeechStopped.CREATOR
        }

        override fun toString(): String {
            return "OnSpeechStopped(task=$task)"
        }

    }

    override fun toString(): String {
        return "SpeechEvent(task=$task)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SpeechEvent<*>

        if (task != other.task) return false

        return true
    }

    override fun hashCode(): Int {
        return task?.hashCode() ?: 0
    }


}