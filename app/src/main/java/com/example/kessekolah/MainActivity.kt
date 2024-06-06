package com.example.kessekolah

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.kessekolah.R
import com.example.kessekolah.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor(R.color.transparent)
    }

    private fun setStatusBarColor(colorAttr: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window

            val color = ContextCompat.getColor(this, colorAttr)
            window.statusBarColor = color

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val view = window.decorView
                view.systemUiVisibility =
                    if (isLightColor(color)) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else 0
            }
        }
    }

    private fun isLightColor(color: Int): Boolean {
        val darkness =
            1 - (0.299 * android.graphics.Color.red(color) + 0.587 * android.graphics.Color.green(
                color
            ) + 0.114 * android.graphics.Color.blue(color)) / 255
        return darkness < 0.5
    }
}