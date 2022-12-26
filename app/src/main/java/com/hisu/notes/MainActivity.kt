package com.hisu.notes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.findNavController
import com.hisu.notes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding?= null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onNavigateUp(): Boolean {
        return findNavController(R.id.fragmentContainerView).navigateUp() || super.onNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}