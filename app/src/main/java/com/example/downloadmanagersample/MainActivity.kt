package com.example.downloadmanagersample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.downloadmanagersample.database.Utils
import com.example.downloadmanagersample.databinding.ActivityMainBinding


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

        //download media files
        binding.fab.setOnClickListener {
            Utils.startMediaSync(this.applicationContext)
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

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}