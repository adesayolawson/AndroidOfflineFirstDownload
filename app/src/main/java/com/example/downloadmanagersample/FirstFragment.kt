package com.example.downloadmanagersample

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                            fileName = "snake_river.jpg",
                            filePath = "test",
                            fileType = SyncEnums.FileType.IMAGE,
                            module = SyncEnums.Modules.MEDIA,
                            fileURL = "https://drive.google.com/uc?export=download&id=1Ri2i9IRgsbAy6AYvDvULIjwCroBwZ1Fw",
                            fileSize = 0L,
                            fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_PENDING
                        ), FileSync(
                            fileName = "20MB.zip",
                            filePath = "test",
                            fileType = SyncEnums.FileType.IMAGE,
                            module = SyncEnums.Modules.MEDIA,
                            fileURL = "http://ipv4.download.thinkbroadband.com/20MB.zip",
                            fileSize = 0L,
                            fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_PENDING
                        )
                    )
                )


            }
            /*findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)*/
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}