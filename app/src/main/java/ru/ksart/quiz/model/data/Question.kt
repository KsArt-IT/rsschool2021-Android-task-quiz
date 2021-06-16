package ru.ksart.quiz.model.data

data class Question(
    // вопрос
    val question: String = "",
    // ответы
    val answers: List<Answer>,
    // выбранный ответ
    var answerSelected: Int = -1,
)
