package com.example.mainactivity.models

import java.lang.IllegalArgumentException

/*
    frameCount - указывает число возможных фреймов, в которых может находиться форма
    startPosition - предполагаемая начальная позиция формы вдоль оси Х
 */
enum class Shape(val frameCount: Int, val startPosition: Int) {

    //Создаем экзмпляр класса enum
    Tetromino4(2,2){

        /*
            frameNumber - определяет фрейм, который будет возвращаться
         */
        override fun getFrame(frameNumber: Int): Frame {
            return when(frameNumber){
                0 -> Frame(4).addRow("1111")

                1 -> Frame(1)
                    .addRow("1")
                    .addRow("1")
                    .addRow("1")
                    .addRow("1")

                else -> throw IllegalArgumentException("$frameNumber is adn invalid frame number")
            }
        }
    },

    Tetromino1(1, 1){
        override fun getFrame(frameNumber: Int): Frame {
            return Frame(2)
                .addRow("11")
                .addRow("11")
        }
    },

    Tetromino2(2, 1){
        override fun getFrame(frameNumber: Int): Frame {
            return when(frameNumber){
                0 -> Frame(3)
                    .addRow("110")
                    .addRow("011")

                1 -> Frame(2)
                    .addRow("01")
                    .addRow("11")
                    .addRow("10")

                else -> throw IllegalArgumentException("$frameNumber is adn invalid frame number")
            }
        }
    },

    Tetramino3(2, 1){
        override fun getFrame(frameNumber: Int): Frame {
            return when(frameNumber){
                0 -> Frame(3)
                    .addRow("011")
                    .addRow("110")

                1 -> Frame(2)
                    .addRow("10")
                    .addRow("11")
                    .addRow("01")

                else -> throw IllegalArgumentException("$frameNumber is adn invalid frame number")
            }
        }
    },

    Tetramino5(4,1){
        override fun getFrame(frameNumber: Int): Frame {
            return when(frameNumber){
                0 -> Frame(3)
                    .addRow("010")
                    .addRow("111")

                1 -> Frame(2)
                    .addRow("10")
                    .addRow("11")
                    .addRow("10")

                2 -> Frame(3)
                    .addRow("111")
                    .addRow("010")

                3 -> Frame(2)
                    .addRow("01")
                    .addRow("11")
                    .addRow("01")

                else -> throw IllegalArgumentException("$frameNumber is adn invalid frame number")

            }
        }
    },

    Tetramino6(4, 1){
        override fun getFrame(frameNumber: Int): Frame {
            return when(frameNumber){
                0 -> Frame(3)
                    .addRow("001")
                    .addRow("111")

                1 -> Frame(2)
                    .addRow("10")
                    .addRow("10")
                    .addRow("11")

                2 -> Frame(3)
                    .addRow("111")
                    .addRow("100")

                3 -> Frame(2)
                    .addRow("11")
                    .addRow("01")
                    .addRow("01")

                else -> throw IllegalArgumentException("$frameNumber is adn invalid frame number")
            }
        }
    };

    abstract fun getFrame(frameNumber: Int): Frame


}