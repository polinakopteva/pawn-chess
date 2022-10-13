package com.polina.projects

import com.polina.projects.entity.*

fun main() {
    println("Pawns-Only Chess")
    println("First Player's name:")
    val firstPlayerInputName = readln()
    val firstPlayer = Player(firstPlayerInputName, WHITE)
    println("Second Player's name:")
    val secondPlayerInputName = readln()
    val secondPlayer = Player(secondPlayerInputName, BLACK)

    val chessboard = Chessboard()
    chessboard.printChessboard()

    val userValidator = InputValidator()
    var currentPlayerName = firstPlayer.name
    var color = firstPlayer.color

    while (true) {
        println("$currentPlayerName's turn:")
        val userInput = readln()

        if (userInput == "exit") {
            println("Bye!")
            break
        }

        if (!chessboard.checkCell(userInput, color)) {
            val colorName = if (color == WHITE) "white" else "black"
            val startPosition = userInput.substring(0..1)
            println("No $colorName pawn at $startPosition")
            continue
        }

        if (!userValidator.isValid(userInput)) {
            println("Invalid input!")
            continue
        }

        if (!chessboard.checkEnPassant(userInput, color) && !chessboard.checkCapture(userInput, color)
            && !chessboard.checkStep(userInput, color)) {
            println("Invalid input")
        } else {
            chessboard.makeStep(userInput)
            currentPlayerName = if (currentPlayerName == firstPlayer.name) secondPlayer.name else firstPlayer.name
            color = if (color == WHITE) BLACK else WHITE
            chessboard.printChessboard()
            val isStalemate = chessboard.checkStalemate(color)
            val isWin = chessboard.checkWin()

            if (isStalemate || isWin) {
                println("Bye!")
                break
            }
        }
    }
}