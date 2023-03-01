package com.example.mainactivity.models

import com.example.mainactivity.helper.func.array2dOfByte

/*
    width - целочисленное свойство задающее ширину фрейма ( число столбцов в байтовом массиве фрейма )
    data - список элементов массива в пространстве значений ByteArray
 */
class Frame(private val width: Int) {

    val data: ArrayList<ByteArray> = ArrayList()

    //Обрабатывает строку, преобразуя каждый отдельный символ в байтовое представление и добавляет его в байтовый массив, после чего байтовый массив добавляется в список данных
    fun addRow(byteStr: String): Frame{

        val row = ByteArray(byteStr.length)

        for(index in byteStr.indices){
            row[index] = "${byteStr[index]}".toByte()
        }
        data.add(row)
        return this
    }

    fun as2dByteArray(): Array<ByteArray>{
        val bytes = array2dOfByte(data.size, width)
        return data.toArray(bytes)
    }
}