package com.example.mainactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.example.mainactivity.databinding.ActivityGameBinding
import com.example.mainactivity.models.AppModel
import com.example.mainactivity.storage.AppPreferences
import com.example.mainactivity.view.TetrisView

class GameActivity : AppCompatActivity() {

    lateinit var binding: ActivityGameBinding

    var appPreferences: AppPreferences? = null

    private lateinit var tetrisView: TetrisView
    private val appModel: AppModel = AppModel()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appPreferences = AppPreferences(this)
        appModel.setPreferences(appPreferences!!)

        tetrisView = binding.viewTetris
        tetrisView.setActivity(this)
        tetrisView.setModel(appModel)
        tetrisView.setOnTouchListener(this::onTetrisViewTouch)
        binding.btnRestart.setOnClickListener(this::btnRestartClick)


        updateHighScore()
        updateCurrentScore()

    }

    private fun btnRestartClick(view: View){
        appModel.restartGame()
    }

    private fun onTetrisViewTouch(view: View, event: MotionEvent): Boolean{
        if(appModel.isGameOver() || appModel.isGameAwaitingStart()){
            appModel.startGame()
            tetrisView.setGameCommandWithDelay(AppModel.Motions.DOWN)
        }else if(appModel.isGameActive()){
            when(resolveTouchDirection(view, event)){
                0 -> moveTetromino(AppModel.Motions.LEFT)
                1 -> moveTetromino(AppModel.Motions.ROTATE)
                2 -> moveTetromino(AppModel.Motions.DOWN)
                3 -> moveTetromino(AppModel.Motions.RIGHT)
            }
        }
        return true
    }

    private fun resolveTouchDirection(view: View, event: MotionEvent): Int{
        val x = event.x / view.width
        val y = event.y / view.height
        val direction: Int

        direction = if(y > x){
            if(x > 1 - y) 2 else 0
        }else{
            if(x > 1 - y) 3 else 1
        }
        return direction
    }

    private fun moveTetromino(motion: AppModel.Motions){
        if(appModel.isGameActive()){
            tetrisView.setGameCommand(motion)
        }
    }


    private fun updateHighScore(){
        binding.tvHighScore.text = "${appPreferences?.getHighScore()}"
    }

    private fun updateCurrentScore(){
        binding.tvCurrentScore.text = "0"
    }
}