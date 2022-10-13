package com.polina.projects.entity

const val SIZE = 7
const val WHITE_PAWN = "W"
const val BLACK_PAWN = "B"
const val EMPTY = " "
const val START_BLACK_ROW = 1
const val START_WHITE_ROW = 6
const val WHITE = 0
const val BLACK = 1

class Chessboard {
    private var previousTwoStepIndexRow = -1
    private var previousTwoStepIndexCol = -1
    private var isEnPassant = false
    private val chessboard = createChessboard()

    fun checkCell(userInput: String, color: Int): Boolean {
        val rowIndex = getRow(userInput[1])
        val colIndex = getColumn(userInput[0].toString())
        val curCell = chessboard[rowIndex][colIndex]
        if ((curCell == WHITE_PAWN && color == WHITE) || (curCell == BLACK_PAWN && color == BLACK)) {
            return true
        }
        return false
    }

    fun checkEnPassant(userInput: String, color: Int): Boolean {
        if (previousTwoStepIndexRow == -1 && previousTwoStepIndexCol == -1) {
            return false
        }

        val oldIndexRow = getRow(userInput[1])
        val oldIndexCol = getColumn(userInput[0].toString())

        val newIndexRow = getRow(userInput[3])
        val newIndexCol = getColumn(userInput[2].toString())
        val newCell = chessboard[newIndexRow][newIndexCol]

        if (newCell != EMPTY) {
            return false
        }

        if ((Math.abs(oldIndexRow - newIndexRow) != 1) || (Math.abs(oldIndexCol - newIndexCol) != 1)) {
            return false
        }

        val underCellIndexRow = if (color == WHITE) newIndexRow + 1 else newIndexRow - 1
        val underCell = chessboard[underCellIndexRow][newIndexCol]

        if (!(color == WHITE && underCell == BLACK_PAWN) && !(color == BLACK && underCell == WHITE_PAWN)) {
            return false
        }

        if (underCellIndexRow != previousTwoStepIndexRow || newIndexCol != previousTwoStepIndexCol) {
            previousTwoStepIndexRow = -1
            previousTwoStepIndexCol = -1
            isEnPassant = false
            return false
        }
        isEnPassant = true
        return true
    }

    fun checkCapture(userInput: String, color: Int): Boolean {
        val oldIndexRow = getRow(userInput[1])
        val oldIndexCol = getColumn(userInput[0].toString())

        val newIndexRow = getRow(userInput[3])
        val newIndexCol = getColumn(userInput[2].toString())
        val newCell = chessboard[newIndexRow][newIndexCol]

        if ((Math.abs(oldIndexRow - newIndexRow) != 1) || (Math.abs(oldIndexCol - newIndexCol) != 1)) {
            return false
        }

        if (!(color == WHITE && newCell == BLACK_PAWN) && !(color == BLACK && newCell == WHITE_PAWN)) {
            return false
        }

        if (!(color == WHITE && oldIndexRow > newIndexRow) && !(color == BLACK && oldIndexRow < newIndexRow)) {
            return false
        }
        return true
    }

    fun checkStep(userInput: String, color: Int): Boolean {
        previousTwoStepIndexRow = -1
        previousTwoStepIndexCol = -1

        val oldRow = castToInt(userInput, 1)
        val newRow = castToInt(userInput, 3)

        val rowIndexNew = getRow(userInput[3])
        val colIndexNew = getColumn(userInput[2].toString())
        val newCell = chessboard[rowIndexNew][colIndexNew]

        if (userInput[0] != userInput[2]) {
            return false
        }
        val stepLength = Math.abs(oldRow - newRow)
        if (stepLength > 2 || stepLength < 1) {
            return false
        }
        if ((color == BLACK && newRow >= oldRow) || (color == WHITE && newRow <= oldRow)) {
            return false
        }
        if (stepLength == 1 && newCell == EMPTY) {
            return true
        }
        val previousPosition = getRow(userInput[1])
        if ((color == BLACK && previousPosition == START_BLACK_ROW && newCell == EMPTY)
            || (color == WHITE && previousPosition == START_WHITE_ROW && newCell == EMPTY)) {
            previousTwoStepIndexRow = rowIndexNew
            previousTwoStepIndexCol = colIndexNew
            return true
        }
        return false
    }

    fun makeStep(userInput: String) {
        val rowIndexOld = getRow(userInput[1])
        val colIndexOld = getColumn(userInput[0].toString())
        val cellOld = chessboard[rowIndexOld][colIndexOld]
        val rowIndexNew = getRow(userInput[3])
        val colIndexNew = getColumn(userInput[2].toString())
        chessboard[rowIndexNew][colIndexNew] = cellOld
        chessboard[rowIndexOld][colIndexOld] = EMPTY
        if (previousTwoStepIndexRow != -1 && previousTwoStepIndexCol != -1 && isEnPassant) {
            chessboard[previousTwoStepIndexRow][previousTwoStepIndexCol] = EMPTY
            previousTwoStepIndexRow = -1
            previousTwoStepIndexCol = -1
            isEnPassant = false
        }
    }

    fun checkStalemate(color: Int): Boolean {
        var countWhite = 0
        var countBlack = 0
        for (row in chessboard) {
            for (cell in row) {
                if (cell == WHITE_PAWN) {
                    countWhite += 1
                } else if (cell == BLACK_PAWN) {
                    countBlack += 1
                }
            }
        }

        if (countBlack == 0 || countWhite == 0) {
            return false
        }

        for (i in chessboard.indices) {
            for (j in chessboard[i].indices) {
                val cell = chessboard[i][j]
                if (color == WHITE && cell == WHITE_PAWN) {
                    if (chessboard[i-1][j] == EMPTY) {
                        return false
                    }

                    val leftDiagonalIndex = j - 1
                    val rightDiagonalIndex = j + 1

                    if ((leftDiagonalIndex >= 0 && chessboard[i-1][leftDiagonalIndex] == BLACK_PAWN) ||
                        (rightDiagonalIndex <= SIZE && chessboard[i-1][rightDiagonalIndex] == BLACK_PAWN)) {
                        return false
                    }
                }
                if (color == BLACK && cell == BLACK_PAWN) {
                    if (chessboard[i+1][j] == EMPTY) {
                        return false
                    }

                    val leftDiagonalIndex = j - 1
                    val rightDiagonalIndex = j + 1

                    if ((leftDiagonalIndex >= 0 && chessboard[i-1][leftDiagonalIndex] == WHITE_PAWN) ||
                        (rightDiagonalIndex <= SIZE && chessboard[i-1][rightDiagonalIndex] == WHITE_PAWN)) {
                        return false
                    }
                }
            }
        }
        println("Stalemate!")
        return true
    }

    fun checkWin(): Boolean {
        var countWhite = 0
        var countBlack = 0
        for (row in chessboard) {
            for (cell in row) {
                if (cell == WHITE_PAWN) {
                    countWhite += 1
                } else if (cell == BLACK_PAWN) {
                    countBlack += 1
                }
            }
        }
        if ((countWhite == 0 && countBlack > 0) || (BLACK_PAWN in chessboard[SIZE])) {
            println("Black Wins!")
            return true
        }

        if ((countBlack == 0 && countWhite > 0) || (WHITE_PAWN in chessboard[0])) {
            println("White Wins!")
            return true
        }
        return false
    }

    fun printChessboard() {
        for (rowIndex in chessboard.indices) {
            val row = chessboard[rowIndex]
            println("  +---+---+---+---+---+---+---+---+")
            val currentRowNumber = SIZE - rowIndex + 1
            print("$currentRowNumber ")
            for (colIndex in row.indices) {
                val element = row[colIndex]
                print("| $element ")
            }
            print("|")
            println()
        }
        println("  +---+---+---+---+---+---+---+---+")
        println("    a   b   c   d   e   f   g   h  ")
    }

    private fun createChessboard(): MutableList<MutableList<String>> {
        val chessboard = mutableListOf<MutableList<String>>()
        for (i in 0..SIZE) {
            val row = mutableListOf<String>()
            for (j in 0..SIZE) {
                when (i) {
                    START_BLACK_ROW -> row.add(BLACK_PAWN)
                    START_WHITE_ROW -> row.add(WHITE_PAWN)
                    else -> row.add(EMPTY)
                }
            }
            chessboard.add(row)
        }
        return chessboard
    }

    private fun getColumn(letter: String): Int {
        return when(letter.lowercase()){
            "a" -> 0
            "b" -> 1
            "c" -> 2
            "d" -> 3
            "e" -> 4
            "f" -> 5
            "g" -> 6
            "h" -> 7
            else -> -1
        }
    }

    private fun getRow(index: Char): Int {
        val numeric = castCharToInt(index)
        return SIZE - numeric + 1
    }

    private fun castToInt(input: String, index: Int): Int {
        return input[index].toString().toInt()
    }

    private fun castCharToInt(input: Char): Int {
        return input.toString().toInt()
    }
}