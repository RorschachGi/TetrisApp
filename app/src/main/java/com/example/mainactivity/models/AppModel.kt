package com.example.mainactivity.models

import android.graphics.Point
import com.example.mainactivity.constants.CellConstants
import com.example.mainactivity.constants.FieldConstants
import com.example.mainactivity.helper.func.array2dOfByte
import com.example.mainactivity.storage.AppPreferences
import kotlin.concurrent.fixedRateTimer

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
        Принимает действие в качестве аргумента и генерирует поле
     */
    fun generateField(action: String){
        //проверяет находится ли игра в активном состоянии:
        //если она активна, то извлекаем номер фрейма и координаты блока
        if(isGameActive()){
            resetField();
            var frameNumber: Int? = currentBlock?.frameNumber
            val coordinate: Point? = Point()
            coordinate?.x = currentBlock?.position?.x
            coordinate?.y = currentBlock?.position?.y

            //Определяем запрашиваемое действие и меняем координаты блока
            when(action){
                Motions.LEFT.name -> {
                    coordinate?.x = currentBlock?.position?.x?.minus(1)
                }

                Motions.RIGHT.name -> {
                    coordinate?.x = currentBlock?.position?.x?.plus(1)
                }

                Motions.DOWN.name -> {
                    coordinate?.y = currentBlock?.position?.y?.plus(1)
                }

                Motions.ROTATE.name -> {
                    frameNumber = frameNumber?.plus(1)
                    if(frameNumber != null){
                        if(frameNumber >= currentBlock?.frameCount as Int){
                            frameNumber = 0
                        }
                    }
                }
            }
            //Проверка действия на действительность
            //Если действие не является действительным, блок фиксируется в поле с помощью translateBlock()
            if(!moveValid(coordinate as Point, frameNumber)){
                translateBlock(currentBlock?.position as Point, currentBlock?.frameNumber as Int)
                if(Motions.DOWN.name == action){
                    boostScore()
                    persistCellData()
                    assessField()
                    generateNextBlock()
                    if(!blockAdditionPossible()){
                        currentState = Statuses.OVER.name
                        currentBlock = null
                        resetField(false)
                    }
                }
            }else{
                if(frameNumber != null){
                    translateBlock(coordinate, frameNumber)
                    currentBlock?.setState(frameNumber, coordinate)
                }
            }
        }
    }

    private fun resetField(ephemeraCellsOnly: Boolean = true){
        for(i in 0 until FieldConstants.ROW_COUNT.value){
            (0 until FieldConstants.COLUMN_COUNT.value)
                .filter {!ephemeraCellsOnly || field[i][it] ==
                            CellConstants.EPHEMERAL.value}
                .forEach{field[i][it] = CellConstants.EMPTY.value}
        }
    }

    private fun persistCellData(){
        for(i in 0 until field.size){
            for(j in 0 until field[i].size){
                var status = getCellStatus(i, j)
                if(status == CellConstants.EPHEMERAL.value){
                    status = currentBlock?.staticValue
                    setCellStatus(i, j, status)
                }
            }
        }
    }

    //Cканирование строк и проверка заполняемости ячеек
    private fun assessField(){
        for(i in 0 until field.size){
            var emptyCells = 0
            for(j in 0 until field[i].size){
                val status = getCellStatus(i , j)
                val isEmpty = CellConstants.EMPTY.value == status
                if(isEmpty){
                    emptyCells++
                }
                if(emptyCells == 0){
                    shiftRows(i)
                }
            }
        }
    }

    private fun translateBlock(position: Point, frameNumber: Int){
        synchronized(field){
            val shape: Array<ByteArray>? = currentBlock?.getShape(frameNumber)
            if (shape != null){
                for(i in shape.indices){
                    for(j in 0 until  shape[i].size){
                        val y = position.y + i
                        val x = position.x + j
                        if(CellConstants.EMPTY.value != shape[i][j]){
                            field[y][x] = shape[i][j]
                        }
                    }
                }
            }
        }
    }

    //Проверка на то, что поле еще не заполнено и блок может переместиться
    private fun blockAdditionPossible() : Boolean{
        if(!moveValid(currentBlock?.position as Point, currentBlock?.frameNumber)){
            return false
        }
        return true
    }

    private fun shiftRows(nToRow: Int){
        if(nToRow > 0){
            for(j in nToRow - 1 downTo 0){
                for(m in 0 until field[j].size){
                    setCellStatus(j + 1, m, getCellStatus(j, m))
                }
            }
        }
        for(j in 0 until field[0].size){
            setCellStatus(0, j, CellConstants.EMPTY.value)
        }
    }

    fun startGame(){
        if(!isGameActive()){
            currentState = Statuses.ACTIVE.name
            generateNextBlock()
        }
    }

    fun restartGame(){
        resetModel()
        startGame()
    }

    fun endGame(){
        score = 0
        currentState = AppModel.Statuses.OVER.name
    }

    private fun resetModel(){
        resetField(false)
        currentState = Statuses.AWAITING_START.name
        score = 0
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