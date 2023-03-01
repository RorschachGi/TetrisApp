package com.example.mainactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mainactivity.databinding.ActivityGameBinding
import com.example.mainactivity.storage.AppPreferences

class GameActivity : AppCompatActivity() {

    lateinit var binding: ActivityGameBinding

    var appPreferences: AppPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateHighScore()
        updateCurrentScore()

    }

    private fun updateHighScore(){
        binding.tvHighScore.text = "${appPreferences?.getHighScore()}"
    }

    private fun updateCurrentScore(){
        binding.tvCurrentScore.text = "0"
    }
}