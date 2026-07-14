package org.fossify.filemanager.activities

import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import org.fossify.filemanager.R

class VideoPlayerActivity : SimpleActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

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
