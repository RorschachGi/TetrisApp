package com.example.mainactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.mainactivity.databinding.ActivityMainBinding
import com.example.mainactivity.storage.AppPreferences
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNewGame.setOnClickListener(this::onBtnNewGameClick)

        binding.btnExit.setOnClickListener(this::onBtnExitClick)

        binding.btnResetScore.setOnClickListener(this::onBtnResetScoreClick)
    }

    private fun onBtnNewGameClick(view: View){
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    private fun onBtnResetScoreClick(view: View) {
        val preferences = AppPreferences(this)
        preferences.clearHighScore()
        Snackbar.make(view, "Score successfully reset", Snackbar.LENGTH_SHORT).show()
        binding.tvHighScore.text = "High score: ${preferences.getHighScore()}"
    }

    private fun onBtnExitClick(view: View){
        finish()
    }
}