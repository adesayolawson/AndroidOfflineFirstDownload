package com.example.downloadmanagersample.exoplayer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.contextaware.withContextAvailable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.navArgs
import com.example.downloadmanagersample.R
import com.example.downloadmanagersample.database.Utils
import com.example.downloadmanagersample.database.model.fileSync.FileSync
import com.example.downloadmanagersample.database.model.fileSync.FileSyncRepo
import com.example.downloadmanagersample.database.model.mediaData.MediaDataRepository
import com.example.downloadmanagersample.databinding.ActivityExoPlayerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExoPlayerActivity : AppCompatActivity() {

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityExoPlayerBinding.inflate(layoutInflater)
    }
    private val playbackStateListener: Player.Listener = playbackStateListener()
    private var player: Player? = null
    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var playbackPosition = 0L
    private lateinit var mediaItem: MediaItem
    private val args: ExoPlayerActivityArgs by navArgs()
    private lateinit var currentPlayingFile: FileSync
    private lateinit var currentLanguageSelected: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        viewBinding.nextBtn.setOnClickListener {
            checkLanguage()
        }
        initPlayerInformation()
    }

    public override fun onStart() {
        super.onStart()

    }

    public override fun onResume() {
        super.onResume()

        if (player == null && this::currentPlayingFile.isInitialized) {
            initializePlayer(currentPlayingFile)
        }
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun checkLanguage(): String {
        Toast.makeText(this, viewBinding.autoCompleteTextView.text.toString(), Toast.LENGTH_SHORT)
            .show()
        return viewBinding.autoCompleteTextView.text.toString()

    }

    private fun handleDownloadButtonVisibility(fileSync: FileSync) {

    }


    private fun initializePlayer(fileSync: FileSync) {
        // ExoPlayer implements the Player interface
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                viewBinding.videoView.player = exoPlayer
                //get the saved file from the file sync object
                val file = FileSyncRepo(this).getFileFromFileSync(this, fileSync)

                mediaItem = if (file == null) {
                    MediaItem.fromUri(fileSync.fileURL ?: "")
                } else {
                    MediaItem.fromUri(file.toUri())
                }

                // please note that android determines the package name
                /*                val file = File(
                                    this.getExternalFilesDir(null)?.absoluteFile,
                //                    "Music/test_audio_1/Nonstop.mp3"

                                    "Music/test_audio_2/Space Cadet.mp3"

                                    // in reality the above code will navigate to
                                    // downloaded_media/video/hausa/play_me.mp4
                //                   " downloaded_media/${args.fileSync.fileName}/${args.fileSync.language}/${args.fileSync.fileName}"
                                )



                                if (file.exists()) {
                                    mediaItem = MediaItem.fromUri(file.toString())
                                } else if (!file.exists()) {
                                    mediaItem = MediaItem.fromUri(args.fileSync.fileURL.toString())

                                }
                //                Toast.makeText(this, "${file.toURI()}", Toast.LENGTH_SHORT).show()
                */


                exoPlayer.setMediaItems(listOf(mediaItem), mediaItemIndex, playbackPosition)
                exoPlayer.playWhenReady = false
                exoPlayer.addListener(playbackStateListener) // Add this line
                exoPlayer.prepare()
            }
    }


    private fun releasePlayer() {
        player?.let { player ->
            playbackPosition = player.currentPosition
            mediaItemIndex = player.currentMediaItemIndex
            playWhenReady = player.playWhenReady

            player.removeListener(playbackStateListener)


            player.release()
        }
        player = null
    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                ExoPlayer.STATE_IDLE -> {

                    val builder = AlertDialog.Builder(this@ExoPlayerActivity)
                    builder.setTitle("Media Player")
                    builder.setMessage("There Was An Error While Media Video. Do You Want To Retry?")
                    builder.setPositiveButton("YES") { _, _ ->
                        player?.prepare()
                        player?.playWhenReady
                    }
                    builder.setNegativeButton("NO") { _, _ -> }
                    builder.setCancelable(false)

                    builder.show()
                    Log.d("MYHTTP", "idle...")

                }

                ExoPlayer.STATE_BUFFERING -> {
                    Log.d("MYHTTP", "buffering...")

                }

                ExoPlayer.STATE_READY -> {
                    "ExoPlayer.STATE_READY-"
                }

                ExoPlayer.STATE_ENDED -> {

                    val builder = AlertDialog.Builder(this@ExoPlayerActivity)
                    builder.setTitle("Media Player")
                    builder.setMessage("The Media File Has Ended")
                    builder.setCancelable(false)

                    builder.setPositiveButton("OKAY") { _, _ -> }
                    builder.show()
                }

                else -> {}
            }
        }
    }

    private fun initPlayerInformation() {
        lifecycleScope.launch(Dispatchers.Main) {
            //this would ideally be done in a proper manner
            val mediaData = withContext(Dispatchers.IO) {
                MediaDataRepository(this@ExoPlayerActivity).getMediaDataByGroupName(args.fileName)
            }
            if (mediaData.isEmpty()) {
                return@launch
            }
            FileSyncRepo(this@ExoPlayerActivity).getFileSyncByNameLive(mediaData.map {
                it.fileName ?: ""
            }).observe(this@ExoPlayerActivity) { fileSyncs ->
                if (fileSyncs.isEmpty()) {
                    return@observe
                }
                val languageToFileSyncMap = mutableMapOf<String, FileSync>()
                fileSyncs.forEach { fileSync ->
                    languageToFileSyncMap[
                            mediaData.find { it.fileName == fileSync.fileName }?.language ?: ""
                    ] = fileSync
                }
                setUpDropDown(languageToFileSyncMap)
            }
        }
    }

    private fun setUpDropDown(languageToFileSyncMap: Map<String, FileSync>) {
        val dropdownMenu = viewBinding.autoCompleteTextView
        fun startPlayer(){
            currentLanguageSelected = dropdownMenu.text.toString()
            currentPlayingFile = languageToFileSyncMap[currentLanguageSelected] ?: return
            initializePlayer(currentPlayingFile)
        }

        val languages = languageToFileSyncMap.keys.toTypedArray()
        if (!this::currentPlayingFile.isInitialized){
            dropdownMenu.setText(languages[0])
            startPlayer()
        }
        dropdownMenu.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line, languages
            )
        )

        dropdownMenu.setOnItemClickListener { _, _, _, _ ->
            releasePlayer()
            startPlayer()
        }
    }


//    @SuppressLint("InlinedApi")
//    private fun hideSystemUi() {
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        WindowInsetsControllerCompat(window, viewBinding.videoView).let { controller ->
//            controller.hide(WindowInsetsCompat.Type.systemBars())
//            controller.systemBarsBehavior =
//                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        }
//    }
}