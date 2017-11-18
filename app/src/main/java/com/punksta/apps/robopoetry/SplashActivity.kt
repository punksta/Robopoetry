package com.punksta.apps.robopoetry


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.punksta.apps.robopoetry.screens.writerLists.MainActivity


class SplashActivity : AppCompatActivity() {
    private val playTime = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
    }

    override fun onStart() {
        super.onStart()

        findViewById<View>(R.id.robo_image).expand(playTime, 5f) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

}

private fun View.expand(animateDuration: Long, times: Float, onComplete: () -> Unit) {
    animate()
            .scaleXBy(times)
            .scaleYBy(times)
            .setDuration(animateDuration)
            .withEndAction(onComplete)
            .withStartAction {
                animate()
                        .rotation(360f)
                        .setDuration((animateDuration * 0.8f).toLong())
                        .start()

            }
            .start()
}