package com.denizd.textbasic

import android.app.backup.BackupManager
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.denizd.textbasic.databinding.ActivityMainBinding
import com.denizd.textbasic.fragment.GuideFragment
import com.denizd.textbasic.fragment.QuoteFragment
import com.denizd.textbasic.fragment.SettingsFragment
import com.denizd.textbasic.widget.TextCanvasWidget
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        binding.buttonBackupUpload.setOnClickListener {
            BackupManager(this).dataChanged()
            Snackbar.make(binding.rootLayout, R.string.snack_backup_upload, Snackbar.LENGTH_LONG).show()
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            loadFragment(item.itemId)
        }
    }

    override fun onResume() {
        super.onResume()

        if (supportFragmentManager.fragments.size == 0) {
            binding.bottomNav.selectedItemId = R.id.quotes
            loadFragment(R.id.quotes)
        }
    }

    private fun loadFragment(type: Int): Boolean {
        val fragment = when (type) {
            R.id.quotes -> QuoteFragment()
            R.id.guide -> GuideFragment()
            R.id.settings -> SettingsFragment()
            else -> QuoteFragment()
        }//supportFragmentManager.findFragmentByTag(type) ?: viewModel.getFragment(type)

        if (supportFragmentManager.fragments.isNotEmpty() &&
            supportFragmentManager.fragments[supportFragmentManager.fragments.size-1]
                .tag?.equals(type.toString()) == true
        ) {
            return false
        }

        if (fragment.lifecycle.currentState != Lifecycle.State.INITIALIZED) {
            return false
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, type.toString())
//            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()

//        with (supportFragmentManager) {
//            beginTransaction()
//                .hide(fragments[fragments.size - 1]) // should this be 0?
//                .show(findFragmentByTag(type) ?: TODO())
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                .commit()
//        }
        return true
    }

    override fun onPause() {

        arrayOf(TextCanvasWidget::class.java).forEach { widget ->
            sendBroadcast(Intent(this, widget).also { intent ->
                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, AppWidgetManager.getInstance(this)
                    .getAppWidgetIds(ComponentName(application, widget)))
            })
        }

        super.onPause()
    }
}