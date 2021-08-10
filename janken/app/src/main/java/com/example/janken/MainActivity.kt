package com.example.janken

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.janken.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)
        binding.guButton.setOnClickListener{ onJankenButtonTapped(it) }
        binding.chokiButton.setOnClickListener { onJankenButtonTapped(it) }
        binding.paButton.setOnClickListener { onJankenButtonTapped(it) }
    }
    fun onJankenButtonTapped(view: View) {
        var intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("MY_HAND",view?.id)
        startActivity(intent)
    }
}