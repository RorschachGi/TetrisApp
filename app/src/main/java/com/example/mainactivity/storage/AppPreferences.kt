package com.example.mainactivity.storage

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(ctx: Context) {

    /*Класс Context поддерживает доступ к методу getSharedPreferences(), который получает файл настроек.
    * Файл настроек идентифицируется по имени в строке, переданной, как первый аргумент метода getSharedPreferences()
    * MODE_PRIVATE - только приложение имеет доступ к настройкам*/
    var data: SharedPreferences = ctx.getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)


    //Сохранение наибольшего рекорда игрока
    fun saveHighScore(highScore: Int){
        data.edit().putInt("HIGH_SCORE", highScore).apply()
    }

    //Возвращает наибольшее количество очков
    fun getHighScore(): Int{
        return data.getInt("HIGH_SCORE", 0)
    }

    //Сброс наибольшего количества очков
    fun clearHighScore(){
        data.edit().putInt("HIGH_SCORE", 0).apply()
    }

}