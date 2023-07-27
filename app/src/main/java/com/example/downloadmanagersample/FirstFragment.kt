package com.example.downloadmanagersample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.downloadmanagersample.database.SyncEnums
import com.example.downloadmanagersample.database.model.fileSync.FileSync
import com.example.downloadmanagersample.database.model.fileSync.FileSyncRepo
import com.example.downloadmanagersample.database.model.mediaData.MediaData
import com.example.downloadmanagersample.database.model.mediaData.MediaDataRepository
import com.example.downloadmanagersample.databinding.FragmentFirstBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    companion object {
        val sampleMediaData = listOf(
            MediaData(
                mediaId = "test1",
                fileType = SyncEnums.FileType.AUDIO,
                fileName = "nonstop.mp3",
                language = "English",
                length = null,
                module = null,
                transcript = null,
                userRoles = null,
                mediaGroup = "group1",
                phase = null,
                publicUrl = "https://firebasestorage.googleapis.com/v0/b/bg-audio-video-image.appspot.com/o/audio%2FDrake%20-%20Nonstop.mp3?alt=media&token=89986392-0efd-4db1-a324-b7a164aeb509",
                defaultFlag = 1,
                size = null
            ), MediaData(
                mediaId = "test2",
                fileType = SyncEnums.FileType.AUDIO,
                fileName = "wanna_be_starting_something.mp3",
                language = "Hausa",
                length = null,
                module = null,
                transcript = null,
                userRoles = null,
                mediaGroup = "group1",
                phase = null,
                publicUrl = "https://firebasestorage.googleapis.com/v0/b/bg-audio-video-image.appspot.com/o/audio%2FWanna%20Be%20Startin%20Somethin.mp3?alt=media&token=b11aef66-6426-4105-976d-c42c13050da0",
                defaultFlag = 1,
                size = null
            )
        )
    }

    private var _binding: FragmentFirstBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mediaDataRepository: MediaDataRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mediaDataRepository = MediaDataRepository(requireContext())

        mediaDataRepository.getCountOfDataInDB().observe(viewLifecycleOwner) {
            binding.isDataInserted = it > 0
        }

        binding.buttonInsertMediaData.setOnClickListener {
            //download test file
            insertFilesToDB()
        }

        binding.navigateToExoPlayerButton.setOnClickListener {
            val action = FirstFragmentDirections.actionFirstFragmentToExoPlayerActivity(
                sampleMediaData[0].mediaGroup.toString()
            )
            findNavController().navigate(action)
        }
    }


    private fun insertFilesToDB() {
        //insert test media data to db
        lifecycleScope.launch(Dispatchers.IO) {
            MediaDataRepository(requireContext()).insert(sampleMediaData)
            FileSyncRepo(requireContext()).insertFileSyncs(
                sampleMediaData.map { mediaData ->
                    FileSync(
                        fileName = mediaData.fileName.toString(),
                        filePath = mediaData.mediaGroup.toString(),
                        fileType = mediaData.fileType ?: SyncEnums.FileType.UNKNOWN,
                        module = SyncEnums.Modules.MEDIA,
                        fileURL = mediaData.publicUrl,
                        fileSize = 0L,
                        fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_PENDING

                    )
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}