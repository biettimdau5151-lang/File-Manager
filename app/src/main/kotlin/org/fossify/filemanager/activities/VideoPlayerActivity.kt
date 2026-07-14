package org.fossify.filemanager.activities

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import org.fossify.filemanager.R

class VideoPlayerActivity : SimpleActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var currentRotation = 0
    private var currentSpeed = 1.0f
    private val speeds = floatArrayOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
    private var speedIndex = 2
    private val handler = Handler(Looper.getMainLooper())

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        playerView = findViewById(R.id.player_view)
        val btnSettings = findViewById<ImageButton>(R.id.btn_settings)

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

        setupGestureDetector()

        btnSettings.setOnClickListener { view ->
            showSettingsPopup(view)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupGestureDetector() {
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                val viewWidth = playerView.width
                val tapX = e.x

                if (tapX < viewWidth / 2) {
                    // Double tap left side - rewind 10s
                    player?.let {
                        val newPosition = (it.currentPosition - 10000).coerceAtLeast(0)
                        it.seekTo(newPosition)
                        Toast.makeText(this@VideoPlayerActivity, "-10s", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Double tap right side - forward 10s
                    player?.let {
                        val newPosition = (it.currentPosition + 10000).coerceAtMost(it.duration)
                        it.seekTo(newPosition)
                        Toast.makeText(this@VideoPlayerActivity, "+10s", Toast.LENGTH_SHORT).show()
                    }
                }
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                toggleControls()
                return true
            }
        })

        playerView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun toggleControls() {
        val controller = playerView.findViewById<View>(androidx.media3.ui.R.id.exo_controller)
        if (controller.visibility == View.VISIBLE) {
            controller.visibility = View.GONE
        } else {
            controller.visibility = View.VISIBLE
        }
    }

    private fun showSettingsPopup(view: View) {
        val popup = PopupMenu(this, view)
        popup.menu.add(0, 1, 0, "Xoay màn hình")
        popup.menu.add(0, 2, 1, "Tốc độ: ${currentSpeed}x")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    rotateScreen()
                    true
                }
                2 -> {
                    changeSpeed()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun rotateScreen() {
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
        Toast.makeText(this, "Tốc độ: ${currentSpeed}x", Toast.LENGTH_SHORT).show()
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
