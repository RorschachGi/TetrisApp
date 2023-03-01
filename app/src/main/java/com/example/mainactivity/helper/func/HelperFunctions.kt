package com.example.mainactivity.helper.func

/*
sizeOuter - номер строки создаваемого массива байтов
sizeInner - номер столбца сгенерированного массива байтов
array2dOfByte - генерирует и возвращает новый массив с указанными свойствами
 */
fun array2dOfByte(sizeOuter: Int, sizeInner: Int): Array<ByteArray> = Array(sizeOuter) {ByteArray(sizeInner)}