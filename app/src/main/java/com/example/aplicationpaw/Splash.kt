package com.example.aplicationpaw


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicationpaw.views.login.Login


class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        val videoView = findViewById<VideoView>(R.id.videoView)
        val videoPath = "android.resource://" + packageName + "/" + R.raw.carga2
        videoView.setVideoURI(Uri.parse(videoPath))
        videoView.setOnCompletionListener { mediaPlayer ->
            // Reiniciar el video
            mediaPlayer?.let {
                it.seekTo(0) //
                it.start()
            }
        }
        videoView.start()

        val splashTimeOut = 2000L

        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed({
            startActivity(Intent(this@Splash, Login::class.java))
            finish()
        }, splashTimeOut)
    }
}