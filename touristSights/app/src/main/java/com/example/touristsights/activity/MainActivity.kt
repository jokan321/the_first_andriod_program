package com.example.touristsights.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.touristsights.R
import com.example.touristsights.databinding.ActivityMainBinding
import com.example.touristsights.fragment.ListFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tag = "ListFragment"
        var listFragment = supportFragmentManager.findFragmentByTag(tag)
        if (listFragment == null) {
            listFragment = ListFragment()
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.content, listFragment, tag).commit()
            }
        }
    }
}