package com.kiran.exoplayer

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import kotlin.random.Random

class PlayerWithCacheActivity : AppCompatActivity(), Player.EventListener {

    lateinit var videoUrl: String
    private lateinit var httpDataSourceFactory: HttpDataSource.Factory
    private lateinit var defaultDataSourceFactory: DefaultDataSourceFactory
    private lateinit var cacheDataSourceFactory: DataSource.Factory
    private lateinit var cacheDataSource: CacheDataSource
    private lateinit var simpleExoPlayer: SimpleExoPlayer
    lateinit var progressBar: ProgressBar
    private val simpleCache: SimpleCache = ExoApp.simpleCache

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        progressBar = findViewById(R.id.progressBar)
        intent.extras?.let {
            videoUrl = it.getString(VIDEO_URL, "")
            if(videoUrl.isBlank()) {
                val rand = Random.nextInt(0,3)
                videoUrl = when(rand){
                    0 -> STREAM_URL1
                    1 -> STREAM_URL2
                    else -> STREAM_URL3
                }
            }
        }
    }

    private fun initializePlayer() {
        httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        defaultDataSourceFactory = DefaultDataSourceFactory(
            this, httpDataSourceFactory
        )

        cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(httpDataSourceFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        simpleExoPlayer = SimpleExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory)).build()

        val videoUri = Uri.parse(videoUrl)
        val mediaItem = MediaItem.fromUri(videoUri)
        val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem)

        val playerView = findViewById<PlayerView>(R.id.playerView)
        playerView.player = simpleExoPlayer
        simpleExoPlayer.playWhenReady = true
        simpleExoPlayer.seekTo(0, 0)
        simpleExoPlayer.repeatMode = Player.REPEAT_MODE_OFF
        simpleExoPlayer.setMediaSource(mediaSource, true)
        simpleExoPlayer.prepare()
        simpleExoPlayer.addListener(this)

        /**
         * Handled cache inside Job Intent Service
         */
        /*
        cacheDataSource = CacheDataSource.Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(httpDataSourceFactory)
            .createDataSource()
        val dataSpec = DataSpec(videoUri)
        val progressListener =
            CacheWriter.ProgressListener { requestLength, bytesCached, newBytesCached ->
                val downloadPercentage: Double = (bytesCached * 100.0
                        / requestLength)
                Log.d("VIDEO", "downloadPercentage $downloadPercentage videoUri: $videoUri")
            }
        cacheVideo(dataSpec, progressListener)*/
    }

    /*private fun cacheVideo(
        dataSpec: DataSpec,
        progressListener: CacheWriter.ProgressListener
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            CacheWriter(
                cacheDataSource,
                dataSpec,
                true,
                null,
                progressListener
            ).cache()
        }
    }*/

    private fun releasePlayer() {
        simpleExoPlayer.release()
    }

    public override fun onStart() {
        super.onStart()

        if (Util.SDK_INT > 23) initializePlayer()
    }

    public override fun onResume() {
        super.onResume()

        if (Util.SDK_INT <= 23) initializePlayer()
    }

    public override fun onPause() {
        super.onPause()

        if (Util.SDK_INT <= 23) releasePlayer()
    }

    public override fun onStop() {
        super.onStop()

        if (Util.SDK_INT > 23) releasePlayer()
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        Log.d("PLAYER", "Playing ERROR: ${error.message}")
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        if (reason == Player.STATE_BUFFERING)
            progressBar.visibility = View.VISIBLE
        else if (reason == Player.STATE_READY || reason == Player.STATE_ENDED)
            progressBar.visibility = View.INVISIBLE
    }

    companion object {
        const val STREAM_URL1 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
        const val STREAM_URL2 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
        const val STREAM_URL3 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"
        const val STREAM_URL4 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
    }
}