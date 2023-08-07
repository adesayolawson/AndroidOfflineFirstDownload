package com.example.downloadmanagersample.exoplayer

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.downloadmanagersample.database.SyncEnums
import com.example.downloadmanagersample.database.Utils
import com.example.downloadmanagersample.database.model.fileSync.FileSync
import com.example.downloadmanagersample.database.model.fileSync.FileSyncRepo
import com.example.downloadmanagersample.database.model.mediaData.MediaDataRepository
import com.example.downloadmanagersample.databinding.ActivityExoPlayerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask


class ExoPlayerFragment : Fragment() {

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityExoPlayerBinding.inflate(layoutInflater)
    }
    private enum class StreamingState{ONLINE,OFFLINE}
    private val playbackStateListener: Player.Listener = playbackStateListener()
    private var player: Player? = null
    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var playbackPosition = 0L
    private lateinit var mediaItem: MediaItem
    private val args by navArgs<ExoPlayerFragmentArgs>()
    private lateinit var currentPlayingFileSync: FileSync
    private lateinit var currentLanguageSelected: String
    private val downloadList = mutableListOf<FileSync>()
    private lateinit var streamingState: StreamingState
    private val internetTimeoutTimer= Timer("internet timeout timer",false)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.nextBtn.setOnClickListener {
            checkLanguage()
        }
        initPlayerInformation()
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()

        if (player == null && this::currentPlayingFileSync.isInitialized) {
            initializePlayer(currentPlayingFileSync)
        }
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun checkLanguage(): String {
        Toast.makeText(requireContext(), viewBinding.autoCompleteTextView.text.toString(), Toast.LENGTH_SHORT)
            .show()
        return viewBinding.autoCompleteTextView.text.toString()

    }

    private fun setupDownloadButton(fileSync: FileSync){
        //make download button invisible when download is running or download completed
        viewBinding.isDownloadAvailable = !(fileSync.fileSyncState == SyncEnums.FileSyncState.DOWNLOAD_RUNNING||
                fileSync.fileSyncState == SyncEnums.FileSyncState.DOWNLOAD_SUCCESSFUL )
        //start file download
        viewBinding.buttonDownload.setOnClickListener {
            Utils.startMediaSync(requireActivity().applicationContext,fileSync)
            downloadList.add(fileSync)
        }
    }


    private fun initializePlayer(fileSync: FileSync) {
        // ExoPlayer implements the Player interface
        player = ExoPlayer.Builder(requireContext())
            .build()
            .also { exoPlayer ->
                viewBinding.videoView.player = exoPlayer
                //get the saved file from the file sync object
                val file = FileSyncRepo(requireContext()).getFileFromFileSync(requireContext(), fileSync)

                mediaItem = if (file == null) {
                    streamingState = StreamingState.ONLINE
                    if (!isNetworkAvailable()){
                        Toast.makeText(requireContext(), "internet issue", Toast.LENGTH_SHORT).show()
                        return}
                    MediaItem.fromUri(fileSync.fileURL ?: "")
                } else {
                    streamingState = StreamingState.OFFLINE
                    MediaItem.fromUri(file.toUri())
                }


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
                    when(streamingState){
                        StreamingState.ONLINE->{
                            if (!isNetworkAvailable()){
                                //show message to inform user that internet has been disconnected
                                Toast.makeText(requireContext(), "internet disconnected please find network", Toast.LENGTH_SHORT).show()
                                //5 second timer to let the user find good network before closing
                                val timerTask = object :TimerTask(){
                                    override fun run() {
                                        activity?.runOnUiThread{
                                            Toast.makeText(
                                                requireContext(),
                                                "internet issue",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            findNavController().navigateUp()
                                        }
                                    }
                                }
                                internetTimeoutTimer.schedule(timerTask,5000)
                                return
                            }
                            Toast.makeText(requireContext(), "error playing file", Toast.LENGTH_SHORT).show()
                        }
                        StreamingState.OFFLINE->{
                            Toast.makeText(requireContext(), "error playing file", Toast.LENGTH_SHORT).show()
                        }
                    }

                }

                ExoPlayer.STATE_BUFFERING -> {
                    Log.d("MYHTTP", "buffering...")
                }

                ExoPlayer.STATE_READY -> {
                    //do nothing
                }

                ExoPlayer.STATE_ENDED -> {
                    val builder = AlertDialog.Builder(requireContext())
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

            //get media data from media group
            val mediaData = withContext(Dispatchers.IO) {
                MediaDataRepository(requireContext()).getMediaDataByGroupName(args.mediaGroup)
            }
            //return if media data is empty and
            //show message indicating error to user @ Abdulmatin
            if (mediaData.isEmpty()) {
                return@launch
            }
            //observe file sync live in the event download is triggered in the background
            FileSyncRepo(requireContext()).getFileSyncByNameLive(mediaData.map {
                it.fileName ?: ""
            }).observe(this@ExoPlayerFragment) { fileSyncs ->
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

                if (this@ExoPlayerFragment::currentPlayingFileSync.isInitialized){
                    //handle live data changes to control button visibility on file sync state change
                    fileSyncs.find { it.fileName == currentPlayingFileSync.fileName }?.let {
                        setupDownloadButton(it)
                    }
                }
            }
        }
    }

    private fun setUpDropDown(languageToFileSyncMap: Map<String, FileSync>) {
        val dropdownMenu = viewBinding.autoCompleteTextView
        fun startPlayer(){
            currentLanguageSelected = dropdownMenu.text.toString()
            currentPlayingFileSync = languageToFileSyncMap[currentLanguageSelected] ?: return
            initializePlayer(currentPlayingFileSync)
        }

        val languages = languageToFileSyncMap.keys.toTypedArray()
        //check if file is assigned and set dropdown to current file sync
        if (!this::currentPlayingFileSync.isInitialized){
            dropdownMenu.setText(languages[0])
            startPlayer()
        }
        dropdownMenu.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line, languages
            )
        )

        dropdownMenu.setOnItemClickListener { _, _, _, _ ->
            releasePlayer()
            playbackPosition = 0L
            startPlayer()
            setupDownloadButton(currentPlayingFileSync)
        }
    }

    //function to check if the internet is available
    fun isNetworkAvailable(): Boolean {
        return try {
            val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            cm.getNetworkCapabilities(cm.activeNetwork)?.hasCapability(NET_CAPABILITY_INTERNET)?:false
        } catch (e: Exception) {
            false
        }
    }

//    private fun
}