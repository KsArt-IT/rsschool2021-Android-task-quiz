package ru.ksart.quiz.presentation.main

import android.R.attr
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import ru.ksart.quiz.R
import ru.ksart.quiz.databinding.ActivityMainBinding
import ru.ksart.quiz.model.data.QuizState
import ru.ksart.quiz.presentation.ui.QuizFragment
import ru.ksart.quiz.presentation.ui.ResultFragment
import ru.ksart.quiz.presentation.viewmodels.QuizViewModel
import ru.ksart.quiz.utils.DebugHelper


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: QuizViewModel by viewModels()
    private var backPressedHold = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(500)
        //установим тему
        setTheme(viewModel.getThemeId())
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        DebugHelper.log("MainActivity|onCreate")
        bindViewModel()
        // вызвать при первом запуске
        if (savedInstanceState == null) {
            initQuestion()
        }
    }

    private fun bindViewModel() {
        viewModel.isShowQuestionFragment.observe(this) {
            showFragmentQuiz()
        }
        // показать результат
        viewModel.isShowResultFragment.observe(this) {
            showFragmentResult()
        }
        // показать тост
        viewModel.isToast.observe(this, ::showToast)
        // рестарт
        viewModel.isRestart.observe(this) {
            initQuestion()
        }
        // отправить
        viewModel.isShare.observe(this, ::share)
        // выход
        viewModel.isExit.observe(this) {
            finish()
        }
    }

    private fun initQuestion() {
        viewModel.initQuestion()
    }

    private fun showFragmentQuiz() {
        setThemesForQuestion()
        supportFragmentManager.commit {
            // разрешить переупорядочивание изменение состояния фрагмента для анаимации
            setReorderingAllowed(true)
            // анимация перехода
/*
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
*/
            replace(R.id.container, QuizFragment(), TAG_QUIZ)
        }
    }

    private fun showFragmentResult() {
        setThemesForQuestion()
        supportFragmentManager.commit {
            // разрешить переупорядочивание изменение состояния фрагмента для анаимации
            setReorderingAllowed(true)
            // анимация перехода
/*
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
*/
            replace(R.id.container, ResultFragment(), TAG_RESULT)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun share(message: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf("ksart.it.001@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject))
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }

        intent.resolveActivity(packageManager)?.let {
            val shareIntent = Intent.createChooser(intent, null)
            startActivity(shareIntent)
        }
    }

    private fun setThemesForQuestion() {
        DebugHelper.log("MainActivity|setThemes")
        theme?.run {
            applyStyle(viewModel.getThemeId(), true)
            window?.run {
                val typedValue = TypedValue()
                // красим statusBarColor
                resolveAttribute(attr.statusBarColor, typedValue, true)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = typedValue.data
                // красим ActionBar
//                resolveAttribute(attr.colorPrimary, typedValue, true)
//                supportActionBar?.setBackgroundDrawable(ColorDrawable(typedValue.data))
                // красим windowBackground
                resolveAttribute(attr.windowBackground, typedValue, true)
                decorView.setBackgroundColor(typedValue.data)
            }
        }
    }

    override fun onBackPressed() {
        when (viewModel.quizState) {
            QuizState.QUIZ -> viewModel.previousQuestion()
            QuizState.RESULT -> viewModel.restartQuestion()
            else -> {
                // выход из приложения
                if (backPressedHold + 3000 > System.currentTimeMillis())
                    super.onBackPressed();
                else
                    Toast.makeText(this, R.string.press_back_exit, Toast.LENGTH_SHORT).show()
                backPressedHold = System.currentTimeMillis()
            }
        }
    }

    companion object {
        private const val TAG_QUIZ = "tag_fragment_quiz"
        private const val TAG_RESULT = "tag_fragment_result"
    }
}
