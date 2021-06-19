package ru.ksart.quiz.presentation.viewmodels

import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.ksart.quiz.R
import ru.ksart.quiz.model.data.Question
import ru.ksart.quiz.model.data.QuizThemes
import ru.ksart.quiz.model.repositories.QuizRepository
import ru.ksart.quiz.utils.DebugHelper
import ru.ksart.quiz.utils.SingleLiveEvent

class QuizViewModel : ViewModel() {
    private val repository = QuizRepository()

    // список вопросов с ответами и балами
    private var questions = emptyList<Question>()

    // номер вопроса
    private var questionCurrent = 0

    // номер вопроса для титула
    val titleN get() = questionCurrent + 1

    // результат
    private var resultQuiz: Triple<Int, Int, Int> = Triple(0, 0, 0)

    // показать тост
    private val _isToast = SingleLiveEvent<String>()
    val isToast: LiveData<String> get() = _isToast

    // показать фрагмент с вопросом
    private val _isShowQuestionFragment = SingleLiveEvent<Unit>()
    val isShowQuestionFragment: LiveData<Unit> get() = _isShowQuestionFragment

    // показать фрагмент с результатом
    private val _isShowResultFragment = SingleLiveEvent<Unit>()
    val isShowResultFragment: LiveData<Unit> get() = _isShowResultFragment

    // показать вопрос
    private val _isShowQuestion = SingleLiveEvent<Question>()
    val isShowQuestion: LiveData<Question> get() = _isShowQuestion

    // показать результат
    private val _isShowResult = SingleLiveEvent<Triple<Int, Int, Int>>()
    val isShowResult: LiveData<Triple<Int, Int, Int>> get() = _isShowResult

    // кнопка назад, активна - не активна
    private val _isEnabledPreviousButton = MutableLiveData<Boolean>(false)
    val isEnabledPreviousButton: LiveData<Boolean> get() = _isEnabledPreviousButton

    // кнопка вперед, активна - не активна
    private val _isEnabledNextButton = MutableLiveData<Pair<Boolean, @StringRes Int>>()
    val isEnabledNextButton: LiveData<Pair<Boolean, Int>> get() = _isEnabledNextButton

    // поделиться
    private val _isShare = SingleLiveEvent<String>()
    val isShare: LiveData<String> get() = _isShare

    // рестарт
    private val _isRestart = SingleLiveEvent<Unit>()
    val isRestart: LiveData<Unit> get() = _isRestart

    // выход из приложения
    private val _isExit = SingleLiveEvent<Unit>()
    val isExit: LiveData<Unit> get() = _isExit

    // список вопросов с ответами
    fun initQuestion() {
        DebugHelper.log("--------------------------")
        DebugHelper.log("QuizViewModel|initQuestion")
        DebugHelper.log("--------------------------")
        questionCurrent = 0
        resultQuiz = Triple(0, 0, 0)
        viewModelScope.launch {
            try {
                // загрузим список вопросов
                questions = repository.loadQuestion()
                // отобразим фрагмент с вопросом
                _isShowQuestionFragment.postValue(Unit)
            } catch (t: Throwable) {
                _isToast.postValue("error: init questions")
            }
        }
    }

    private fun defineButtons() {
        _isEnabledPreviousButton.postValue(questionCurrent > 0)
        _isEnabledNextButton.postValue(
            (questions[questionCurrent].answerSelected >= 0) to
                    if (questionCurrent < questions.lastIndex)
                        R.string.next_button_text
                    else R.string.submit_button_text
        )
    }

    fun getQuestion() {
        defineButtons()
        viewModelScope.launch {
            try {
                DebugHelper.log("QuizViewModel|getQuestion: $questionCurrent = ${questions[questionCurrent].answerSelected}")
                _isShowQuestion.postValue(questions[questionCurrent])
            } catch (t: Throwable) {
                _isToast.postValue("error: show question")
            }
        }
    }

    fun setAnswer(indexAnswer: Int) {
        if (indexAnswer in 0..questions[questionCurrent].answers.lastIndex) {
            // если не равно предыдущему, то меняем
            if (questions[questionCurrent].answerSelected != indexAnswer) {
                questions[questionCurrent].answerSelected = indexAnswer
                DebugHelper.log("QuizViewModel|setAnswer: $questionCurrent = $indexAnswer")
                defineButtons()
            }
        } else {
            _isToast.postValue("error: index questions = $indexAnswer")
        }
    }

    fun showResult() {
        viewModelScope.launch {
            try {
                val count = questions.count { it.answers[it.answerSelected].value > 0 }
                val result = questions.sumOf { it.answers[it.answerSelected].value }
                resultQuiz = Triple(count, questions.size, result)
                _isShowResult.postValue(resultQuiz)
            } catch (t: Throwable) {
                _isToast.postValue("error: show result")
            }
        }
    }

    @StyleRes
    fun getThemeId(): Int {
        return if (questionCurrent > questions.lastIndex) QuizThemes.RESULT.themeId
        else QuizThemes.getTheme(questionCurrent).themeId
    }

    fun share() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = StringBuilder()
                result.append("Результаты Quiz: ")
                result.append("${resultQuiz.third} (${resultQuiz.first}/${resultQuiz.second})")
                result.appendLine().appendLine()
                questions.forEachIndexed { index, question ->
                    result.append("Вопрос №${index + 1}: ${question.question}").appendLine()
                    result.append("Ответ: ")
                    result.append(if (question.answers[question.answerSelected].value > 0) "+" else "-")
                    result.append(question.answers[question.answerSelected].text).appendLine()
                    question.answers.forEachIndexed { indexAnswer, answer ->
                        result.append("   ${indexAnswer + 1}.${answer.text}").appendLine()
                    }
                    result.appendLine()
                }
                _isShare.postValue(result.toString())
            } catch (t: Throwable) {
                _isToast.postValue("error: show share result")
            }
        }
    }

    fun nextQuestion() {
        _isEnabledNextButton.value?.takeIf { it.first }?.let {
            if (questionCurrent in 0..questions.lastIndex) {
                ++questionCurrent
                if (questionCurrent >= questions.size) {
                    // показать результат
                    _isShowResultFragment.postValue(Unit)
                } else {
                    // заблокируем кнопки
                    defineButtons()
                    // отобразить фрагмент
                    _isShowQuestionFragment.postValue(Unit)
                }
            }
        }
        DebugHelper.log("QuizViewModel|nextQuestion: $questionCurrent")
    }

    fun previousQuestion() {
        if (questionCurrent in 1..questions.lastIndex) {
            --questionCurrent
            // заблокируем кнопки
            defineButtons()
            // отобразить фрагмент
            _isShowQuestionFragment.postValue(Unit)
        }
        DebugHelper.log("QuizViewModel|previousQuestion: $questionCurrent")
    }

    fun restart() {
        _isRestart.postValue(Unit)
    }

    fun exit() {
        _isExit.postValue(Unit)
    }
}
