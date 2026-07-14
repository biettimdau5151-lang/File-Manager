package org.fossify.filemanager.activities

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import org.fossify.filemanager.R

class VideoPlayerActivity : SimpleActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        playerView = findViewById(R.id.player_view)

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
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupGestureDetector() {
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                val viewWidth = playerView.width
                val tapX = e.x

                if (tapX < viewWidth / 2) {
                    player?.let {
                        val newPosition = (it.currentPosition - 10000).coerceAtLeast(0)
                        it.seekTo(newPosition)
                        Toast.makeText(this@VideoPlayerActivity, "-10s", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    player?.let {
                        val newPosition = (it.currentPosition + 10000).coerceAtMost(it.duration)
                        it.seekTo(newPosition)
                        Toast.makeText(this@VideoPlayerActivity, "+10s", Toast.LENGTH_SHORT).show()
                    }
                }
                return true
            }
        })

        playerView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
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
