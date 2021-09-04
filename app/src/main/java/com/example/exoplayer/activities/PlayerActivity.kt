/*
 * Created by Lukas (LukaLike) on Sat, Sep 4, '21.
 * Copyright (c) 2021. All rights reserved.
 */

package com.example.exoplayer.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.exoplayer.data.models.Message
import com.example.exoplayer.data.service_factories.MessagesServiceFactory
import com.example.exoplayer.data.services.MessagesService
import com.example.exoplayer.databinding.ActivityPlayerBinding
import com.example.exoplayer.models.Media
import com.example.exoplayer.util.SharedPreferences
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.Util
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.max


private const val TAG = "PlayerActivity"

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityPlayerBinding.inflate(layoutInflater)
    }

    private val playbackStateListener: Player.Listener = playbackStateListener()
    private var player: SimpleExoPlayer? = null

    private lateinit var messagesAPI: MessagesService

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        playWhenReady = sharedPreferences.getAutoPlay()
        currentWindow = sharedPreferences.getWindow()
        playbackPosition = sharedPreferences.getPosition()

        messagesAPI = MessagesServiceFactory.create()
        subscribeToApi()
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun initializePlayer() {
        val trackSelector = DefaultTrackSelector(this).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }

        player = SimpleExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                viewBinding.videoView.player = exoPlayer

                // Add sample media from 'media.exolist.json' to exoPlayer
                val assetManager = assets.open("media.exolist.json")
                val objectArrayString: String = assetManager.bufferedReader().use { it.readText() }
                val mediaList = Gson().fromJson(objectArrayString, Media::class.java)

                mediaList.list?.forEach {
                    exoPlayer.addMediaItem(MediaItem.fromUri(it?.uri.toString()))
                }

                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentWindow, playbackPosition)
                exoPlayer.addListener(playbackStateListener)
                exoPlayer.prepare()
            }
    }

    private fun releasePlayer() {
        player?.run {
            updateStartPosition()
            removeListener(playbackStateListener)
            release()
        }
        player = null
    }

    private fun updateStartPosition() {
        if (player != null) {
            sharedPreferences.setAutoPlay(player!!.playWhenReady)
            sharedPreferences.setWindow(player!!.currentWindowIndex)
            sharedPreferences.setPosition(max(0, player!!.contentPosition))
        }
    }

    @SuppressLint("CheckResult")
    private fun subscribeToApi() {
        Observable.interval(
            DELAY, PERIOD,
            TimeUnit.MILLISECONDS
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ randomNumberEndpoint() }) {
                Log.w(TAG, it)
            }
    }

    @SuppressLint("CheckResult")
    private fun randomNumberEndpoint() {
        val observable: Observable<Message> = messagesAPI.getMessage()
        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .map { result: Message -> result.value }
            .subscribe({
                Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
            }) {
                Log.w(TAG, it)
            }
    }

    companion object {
        private const val DELAY = 1000L
        private const val PERIOD = 30000L
    }

}

private fun playbackStateListener() = object : Player.Listener {

    override fun onPlaybackStateChanged(playbackState: Int) {
        val stateString: String = when (playbackState) {
            ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
            ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
            ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
            ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
            else -> "UNKNOWN_STATE             -"
        }
        Log.d(TAG, "changed state to $stateString")
    }

}