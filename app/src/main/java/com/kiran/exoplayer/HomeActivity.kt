package com.kiran.exoplayer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.kiran.exoplayer.cache.VideoCacheService

const val VIDEO_URL = "VIDEO_URL"
const val VIDEO_LIST = "VIDEO_LIST"
class HomeActivity: AppCompatActivity() {

    private var videoList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initCacheService()

        val intent = Intent(this, PlayerWithCacheActivity::class.java)

        findViewById<Button>(R.id.btn1).setOnClickListener {
            Log.d("HomeActivity", "Clicked Video 1")
            intent.removeExtra(VIDEO_URL)
            intent.putExtra(VIDEO_URL,PlayerWithCacheActivity.STREAM_URL1)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn2).setOnClickListener {
            Log.d("HomeActivity", "Clicked Video 2")
            intent.removeExtra(VIDEO_URL)
            intent.putExtra (VIDEO_URL,PlayerWithCacheActivity.STREAM_URL2)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn3).setOnClickListener {
            Log.d("HomeActivity", "Clicked Video 3")
            intent.removeExtra(VIDEO_URL)
            intent.putExtra (VIDEO_URL,PlayerWithCacheActivity.STREAM_URL3)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn4).setOnClickListener {
            Log.d("Kiran", "Play video from network!")
            val playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra (VIDEO_URL,PlayerWithCacheActivity.STREAM_URL4)
            startActivity(playerIntent)
        }
    }

    private fun initCacheService() {
        videoList.add(PlayerWithCacheActivity.STREAM_URL1)
        videoList.add(PlayerWithCacheActivity.STREAM_URL2)
        videoList.add(PlayerWithCacheActivity.STREAM_URL3)
        val intent = Intent(this, VideoCacheService::class.java)
        intent.putStringArrayListExtra(VIDEO_LIST,videoList)
        VideoCacheService.enqueueWork(this, intent)
    }
}