package org.fossify.filemanager.activities

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class VideoPlayerActivity : SimpleActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var currentRotation = 0
    private var currentSpeed = 1.0f
    private val speeds = floatArrayOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
    private var speedIndex = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(org.fossify.filemanager.R.layout.activity_video_player)

        playerView = findViewById(org.fossify.filemanager.R.id.player_view)
        val btnRotate = findViewById<ImageButton>(org.fossify.filemanager.R.id.btn_rotate)
        val btnSpeed = findViewById<ImageButton>(org.fossify.filemanager.R.id.btn_speed)

        val videoPath = intent.getStringExtra("VIDEO_PATH") ?: run {
            finish()
            return
        }

        val uri = Uri.parse(videoPath)

        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            playerView.player = exoPlayer

            val mediaItem = MediaItem.Builder()
                .setUri(uri)
                .setMimeType(getMimeType(videoPath))
                .build()

            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }

        btnRotate.setOnClickListener {
            rotateVideo()
        }

        btnSpeed.setOnClickListener {
            changeSpeed()
            Toast.makeText(this, "Speed: ${currentSpeed}x", Toast.LENGTH_SHORT).show()
        }
    }

    private fun rotateVideo() {
        currentRotation = (currentRotation + 90) % 360
        requestedOrientation = when (currentRotation) {
            0 -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            90 -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            180 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
            270 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun changeSpeed() {
        speedIndex = (speedIndex + 1) % speeds.size
        currentSpeed = speeds[speedIndex]
        player?.playbackParameters = PlaybackParameters(currentSpeed)
    }

    private fun getMimeType(path: String): String {
        return when {
            path.endsWith(".m3u8", true) -> MimeTypes.APPLICATION_M3U8
            path.endsWith(".ts", true) -> MimeTypes.VIDEO_MP2T
            path.endsWith(".mp4", true) -> MimeTypes.VIDEO_MP4
            path.endsWith(".mkv", true) -> MimeTypes.VIDEO_MATROSKA
            path.endsWith(".webm", true) -> MimeTypes.VIDEO_WEBM
            else -> MimeTypes.VIDEO_MP4
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
