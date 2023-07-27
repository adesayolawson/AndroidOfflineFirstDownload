package com.example.downloadmanagersample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.downloadmanagersample.database.SyncEnums
import com.example.downloadmanagersample.database.model.fileSync.FileSync
import com.example.downloadmanagersample.database.model.fileSync.FileSyncRepo
import com.example.downloadmanagersample.databinding.ActivityMainBinding
import com.example.downloadmanagersample.downloadUtils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    companion object {
        const val FILE_TO_DOWNLOAD_KEY = "file_to_download"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)


        binding.buttonFirst.setOnClickListener {
            insertFileSync()
        }

        binding.fab.setOnClickListener {
            Utils.startMediaSync(this.applicationContext)
        }
    }


    private fun insertFileSync(){
        lifecycleScope.launch(Dispatchers.IO) {
            FileSyncRepo(this@MainActivity).insertFileSyncs(
                listOf(
                    FileSync(
                        fileName = "img1.jpg",
                        filePath = "test",
                        fileType = SyncEnums.FileType.IMAGE,
                        module = SyncEnums.Modules.MEDIA,
                        fileURL = "https://drive.google.com/uc?export=download&id=1Ri2i9IRgsbAy6AYvDvULIjwCroBwZ1Fw",
                        fileSize = 0L,
                        fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_PENDING
                    ), FileSync(
                        fileName = "img2.jpg",
                        filePath = "test",
                        fileType = SyncEnums.FileType.IMAGE,
                        module = SyncEnums.Modules.MEDIA,
                        fileURL = "https://sample-videos.com/img/Sample-jpg-image-5mb.jpg",
                        fileSize = 0L,
                        fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_PENDING
                    ), FileSync(
                        fileName = "img3.jpg",
                        filePath = "test",
                        fileType = SyncEnums.FileType.IMAGE,
                        module = SyncEnums.Modules.MEDIA,
                        fileURL = "https://sample-videos.com/img/Sample-jpg-image-10mb.jpg",
                        fileSize = 0L,
                        fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_PENDING
                    ), FileSync(
                        fileName = "img4.jpg",
                        filePath = "test",
                        fileType = SyncEnums.FileType.IMAGE,
                        module = SyncEnums.Modules.MEDIA,
                        fileURL = "https://sample-videos.com/img/Sample-jpg-image-15mb.jpeg",
                        fileSize = 0L,
                        fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_PENDING
                    ), FileSync(
                        fileName = "img5.jpg",
                        filePath = "test",
                        fileType = SyncEnums.FileType.IMAGE,
                        module = SyncEnums.Modules.MEDIA,
                        fileURL = "https://sample-videos.com/img/Sample-jpg-image-20mb.jpg",
                        fileSize = 0L,
                        fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_PENDING
                    )
                )
            )


        }
    }


/*    private fun downloadMedia(fileSync: FileSync) {
        val fileSyncGson =
            Gson().toJson(fileSync)//convert file sync to json string to be used in worker
        val inputFileSync = Data.Builder().putString(FILE_TO_DOWNLOAD_KEY, fileSyncGson).build()

        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<MediaDownloadWorker>().setConstraints(
            Constraints(requiredNetworkType = NetworkType.CONNECTED)
        ).setInputData(inputFileSync).build()
        WorkManager.getInstance(this).enqueue(oneTimeWorkRequest)
    }*/

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

}