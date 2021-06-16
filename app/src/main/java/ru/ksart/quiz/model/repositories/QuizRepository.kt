package ru.ksart.quiz.model.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.ksart.quiz.model.data.Questions

class QuizRepository {
    suspend fun loadQuestion() = withContext(Dispatchers.IO) {
        Questions.getQuestions()
    }
}