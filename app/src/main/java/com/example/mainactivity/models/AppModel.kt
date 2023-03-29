package com.example.mainactivity.models

import android.graphics.Point
import com.example.mainactivity.constants.CellConstants
import com.example.mainactivity.constants.FieldConstants
import com.example.mainactivity.helper.func.array2dOfByte
import com.example.mainactivity.storage.AppPreferences

class AppModel{

    var score: Int = 0

    //Доступ к данным
    private var preferences: AppPreferences? = null

    //Текущий блок
    var currentBlock: Block? = null

    //Состояние игры
    var currentState: String = Statuses.AWAITING_START.name

    //Двумерный массив поля
    private var field: Array<ByteArray> = array2dOfByte(
        FieldConstants.ROW_COUNT.value,
        FieldConstants.COLUMN_COUNT.value
    )

    fun setPreferences(preferences : AppPreferences){
        this.preferences = preferences
    }

    //Состояние ячейки имеющейся в указанной позиции столба строки в думерном массиве
    fun getCellStatus(row: Int, column: Int): Byte?{
        return field[row][column]
    }

    //Устанавливает состояние ячейки равным указанному байту
    private fun setCellStatus(row: Int, column: Int, status: Byte?){
        if(status != null){
            field[row][column] = status
        }
    }

    //Три метода отслеживания состояния игры
    fun isGameOver(): Boolean{
        return currentState == Statuses.OVER.name
    }

    fun isGameActive(): Boolean{
        return currentState == Statuses.ACTIVE.name
    }

    fun isGameAwaitingStart(): Boolean{
        return currentState == Statuses.AWAITING_START.name
    }

    //Работа со счетчиком очков
    private fun boostScore(){
        score += 10
        if(score > preferences?.getHighScore() as Int){
            preferences?.saveHighScore(score)
        }
    }

    //Создание нового блока
    private fun generateNextBlock(){
        currentBlock = Block.createBlock()
    }

    //Проверка допустимости движения тетрамино
    private fun validTranslation(position: Point, shape: Array<ByteArray>): Boolean{
            return if (position.y < 0 || position.x < 0){
                false
            } else if(position.y + shape.size > FieldConstants.ROW_COUNT.value){
                false
            }else if(position.x + shape[0].size > FieldConstants.COLUMN_COUNT.value){
                false
            }else{
                for(i in 0 until shape.size) {
                    for (j in 0 until shape[i].size) {
                        val y = position.y + i
                        val x = position.x + j
                        if (CellConstants.EMPTY.value != shape[i][j] && CellConstants.EMPTY.value != field[y][x]) {
                            return false
                        }
                    }
                }
                true
            }
    }

    /*
    Применяет validTranslation(), проверяя разрешен ли сделанный ход. Если разрешено true, иначе false
     */
    private fun moveValid(position: Point, frameNumber: Int?): Boolean{
        val shape: Array<ByteArray>? = currentBlock?.getShape(frameNumber as Int)
        return validTranslation(position, shape as Array<ByteArray>)
    }


    /*
        AWAITING_START - состояние игры до ее запуска
        ACTIVE, INACTIVE - состояние игрового процесса ( выполняет или нет )
        OVER - статус принимаемый игрой при завершении
     */
    enum class Statuses{
        ACTIVE, AWAITING_START, INACTIVE, OVER
    }

    //Движение блока
    enum class Motions{
        LEFT, DOWN, RIGHT, ROTATE
    }
}