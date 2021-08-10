package com.example.helloandriod

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.helloandriod.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.tapHere.setOnClickListener {
            binding.textView.text = "ボタンがタップされた！"
        }
    }
}