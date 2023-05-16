package com.denizd.textbasic.activity

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.denizd.textbasic.databinding.ActivityMainBinding
import com.denizd.textbasic.widget.TextCanvasWidget
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this)
        binding.bannerView.loadAd(AdRequest.Builder().build())

        binding.navView.setupWithNavController(
            binding.navHostFragmentContentMain.getFragment<NavHostFragment>().findNavController()
        )
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