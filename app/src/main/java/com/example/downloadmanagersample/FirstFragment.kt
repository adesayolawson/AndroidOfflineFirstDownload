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
import com.example.downloadmanagersample.databinding.FragmentFirstBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                FileSyncRepo(requireContext()).insertFileSyncs(

                    listOf(
                        FileSync(
                            fileName = "Nonstop.mp3",
                            filePath = "test_audio_1",

                            fileType = SyncEnums.FileType.VIDEO,
                            module = SyncEnums.Modules.MEDIA,

                            fileURL = "https://firebasestorage.googleapis.com/v0/b/bg-audio-video-image.appspot.com/o/audio%2FDrake%20-%20Nonstop.mp3?alt=media&token=89986392-0efd-4db1-a324-b7a164aeb509",
                            fileSize = 0L,

                            fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_PENDING

                        ),
                        FileSync(
                            fileName = "Space Cadet.mp3",
                            filePath = "test_audio_2",

                            fileType = SyncEnums.FileType.VIDEO,
                            module = SyncEnums.Modules.MEDIA,

                            fileURL = "https://firebasestorage.googleapis.com/v0/b/bg-audio-video-image.appspot.com/o/audio%2F05.%20Space%20Cadet%20(feat.%20Gunna).mp3?alt=media&token=15c6fd1a-f4b7-4bfe-ba98-acec4b674383",
                            fileSize = 0L,

                            fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_PENDING
                        ),
                        FileSync(
                            fileName = "Dreaming.mp3",
                            filePath = "test_audio_3",

                            fileType = SyncEnums.FileType.DOCUMENT,
                            module = SyncEnums.Modules.MEDIA,

                            fileURL = "https://firebasestorage.googleapis.com/v0/b/bg-audio-video-image.appspot.com/o/audio%2FPop%20Smoke%20-%20Dreaming%20(Meet%20The%20Woo%202)%20(320%20kbps).mp3?alt=media&token=4eb34504-c663-4aa7-8ddc-1485556358aa",
                            fileSize = 0L,

                            fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_PENDING
                        ),
                        FileSync(
                            fileName = "What's My Name.mp3",
                            filePath = "test_audio_4",

                            fileType = SyncEnums.FileType.IMAGE,
                            module = SyncEnums.Modules.MEDIA,

                            fileSize = 0L,

                            fileURL = "https://firebasestorage.googleapis.com/v0/b/bg-audio-video-image.appspot.com/o/audio%2Frihanna_what_s_my_name.mp3?alt=media&token=42f456bc-a99b-4ddb-af74-552fce3b2b29",
                            fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_PENDING
                        ),
                        FileSync(
                            fileName = "Main Title.mp3",
                            filePath = "test_audio_5",

                            fileType = SyncEnums.FileType.AUDIO,
                            module = SyncEnums.Modules.MEDIA,

                            fileSize = 0L,

                            fileURL = "https://firebasestorage.googleapis.com/v0/b/bg-audio-video-image.appspot.com/o/audio%2FMain%20Title%20from%20the%20Netflix%20Series%20Queen%20Charlotte.mp3?alt=media&token=b8a49688-435d-466c-8ab3-fbc616a0215d",
                            fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_PENDING
                        ),
                        FileSync(
                            fileName = "Wanna Be Starting Something.mp3",
                            filePath = "test_audio_6",

                            fileType = SyncEnums.FileType.AUDIO,
                            module = SyncEnums.Modules.MEDIA,

                            fileSize = 0L,

                            fileURL = "https://firebasestorage.googleapis.com/v0/b/bg-audio-video-image.appspot.com/o/audio%2FWanna%20Be%20Startin%20Somethin.mp3?alt=media&token=b11aef66-6426-4105-976d-c42c13050da0",
                            fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_PENDING
                        )


                    )

                )


            }
            /*findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)*/
        }

        binding.navigateToExoPlayerButton.setOnClickListener {

            val dummy_data = FileSync(
                c = "nonstop.mp4",
                filePath = "video",

                fileType = SyncEnums.FileType.AUDIO,
                module = SyncEnums.Modules.MEDIA,

                fileURL = "https://firebasestorage.googleapis.com/v0/b/bg-audio-video-image.appspot.com/o/audio%2FDrake%20-%20Nonstop.mp3?alt=media&token=89986392-0efd-4db1-a324-b7a164aeb509",
                fileSize = 0L,

                fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_PENDING

            )
            val action = FirstFragmentDirections.actionFirstFragmentToExoPlayerActivity(dummy_data)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}