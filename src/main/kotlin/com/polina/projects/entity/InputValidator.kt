package com.polina.projects.entity

class InputValidator {
    private val check = Regex("[a-hA-H][1-8][a-hA-H][1-8]")

    fun isValid(userInput: String): Boolean {
        return check.matches(userInput)
    }
}