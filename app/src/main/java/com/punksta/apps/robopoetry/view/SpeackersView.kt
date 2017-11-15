package com.punksta.apps.robopoetry.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.MATCH_PARENT
import android.widget.TextView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.punksta.apps.robopoetry.R
import com.punksta.apps.robopoetry.model.Robot
import com.squareup.picasso.Picasso

/**
 * Created by stanislav on 1/2/17.
 */

class SpeackersView : HorizontalScrollView {
    var listener: (Robot) -> Unit = {}
    private val map = mutableMapOf<Robot, ImageView>()

    constructor(context: Context?) :
            super(context)

    constructor(context: Context?, attrs: AttributeSet?) :
            super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes)


    private var animation: YoYo.YoYoString? = null;

    init {
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.HORIZONTAL

        addView(linearLayout, MATCH_PARENT, WRAP_CONTENT)
    }

    fun showRobots(list: List<Robot>, active: Robot = list.first(), requestManager: Picasso) {
        map.clear()
        val linearLayout = getChildAt(0) as LinearLayout
        linearLayout.removeAllViews()
        list.map { l ->
            LayoutInflater.from(context).inflate(R.layout.item_speaker, linearLayout, false)
                    .apply {
                        (findViewById<TextView>(R.id.speaker_name))
                        (findViewById<TextView>(R.id.speaker_name) as TextView).text = context.getString(l.nameId)
                        val image = (findViewById<ImageView>(R.id.speaker_image))

                        requestManager.load(l.drawableId)
                                .centerInside()
                                .fit()
                                .into(image)

//                        image.setImageResource(l.drawableId)

                        map[l] = image
                        setOnClickListener {
                            listener(l)
                        }
                    }
        }.forEach {
            linearLayout.addView(it)
        }

        setActive(active)
    }


    private var currentRobot: Robot? = null;


    fun setActive(robot: Robot) {
        map[robot]?.alpha = 1f
        map.filterKeys { it != robot }.forEach { it.value.alpha = 0.5f }
        currentRobot = robot
    }

    private var speachingRobot: Robot? = null;

    fun setSpeacking(robot: Robot) {

        if (speachingRobot != robot) {

            animation?.stop()

            animation = YoYo.with(Techniques.Pulse)
                    .duration(800)
                    .repeat(1000)
                    .delay(0)
                    .playOn(map[robot])

            speachingRobot = robot
        }
    }

    fun clearSpeacking() {
        animation?.stop()
        speachingRobot?.let { map[it] }?.clearAnimation()
        speachingRobot = null
    }
}