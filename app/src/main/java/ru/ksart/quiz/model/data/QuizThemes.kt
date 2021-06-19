package ru.ksart.quiz.model.data

import androidx.annotation.StyleRes
import ru.ksart.quiz.R

enum class QuizThemes(@StyleRes val themeId: Int) {
    FIRST(R.style.Theme_Quiz_First),
    SECOND(R.style.Theme_Quiz_Second),
    THIRD(R.style.Theme_Quiz_Third),
    FOURTH(R.style.Theme_Quiz_Fourth),
    FIFTH(R.style.Theme_Quiz_Fifth),
    SIXTH(R.style.Theme_Quiz_Sixth),
    RESULT(R.style.Theme_Quiz_Result);

    companion object {
        fun getTheme(number: Int): QuizThemes {
            return values()[number % values().lastIndex]
        }
    }
}