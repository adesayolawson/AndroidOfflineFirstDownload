package com.example.downloadmanagersample

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.downloadmanagersample.database.SyncEnums
import com.example.downloadmanagersample.database.model.fileSync.FileSync
import com.example.downloadmanagersample.database.model.fileSync.FileSyncRepo
import com.example.downloadmanagersample.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    companion object {
        const val FILE_TO_DOWNLOAD_KEY = "file_to_download"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


        binding.fab.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                val fileToDownload = withContext(Dispatchers.IO) {
                    FileSyncRepo(this@MainActivity).getFileSyncByModuleAndState(
                        SyncEnums.Modules.MEDIA,
                        SyncEnums.FileSyncState.DOWNLOAD_PENDING
                    )
                }
                if (fileToDownload != null) {
                    downloadMedia(fileToDownload)
                }
            }
        }
    }

    private fun downloadMedia(fileSync: FileSync) {
        val fileSyncGson =
            Gson().toJson(fileSync)//convert file sync to json string to be used in worker
        val inputFileSync = Data.Builder().putString(FILE_TO_DOWNLOAD_KEY, fileSyncGson).build()

        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<MediaDownloadWorker>().setConstraints(
            Constraints(requiredNetworkType = NetworkType.CONNECTED)
        ).setInputData(inputFileSync).build()
        WorkManager.getInstance(this).enqueue(oneTimeWorkRequest)
    }

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

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}