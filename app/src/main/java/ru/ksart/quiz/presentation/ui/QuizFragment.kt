package ru.ksart.quiz.presentation.ui

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.radiobutton.MaterialRadioButton
import ru.ksart.quiz.R
import ru.ksart.quiz.databinding.FragmentQuizBinding
import ru.ksart.quiz.presentation.viewmodels.QuizViewModel
import ru.ksart.quiz.utils.DebugHelper
import ru.ksart.quiz.utils.isAndroid6


@SuppressLint("ResourceType")
class QuizFragment : Fragment(R.layout.fragment_quiz) {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: QuizViewModel by activityViewModels()

    private val radioGroupLayoutParams by lazy {
        RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.MATCH_PARENT,
            RadioGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private val radioButtonColorStateList by lazy {
        if (isAndroid6) {
            ColorStateList.createFromXml(
                resources,
                resources.getXml(R.color.radiobutton_color_selector),
                activity?.theme
            )
        } else {
            ColorStateList.createFromXml(
                resources,
                resources.getXml(R.color.radiobutton_color_selector),
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DebugHelper.log("QuizFragment|onViewCreated")
        bindViewModel()
        initListener()
        viewModel.getQuestion()
    }

    private fun initListener() {
        binding.nextButton.setOnClickListener {
            viewModel.nextQuestion()
        }
        binding.previousButton.setOnClickListener {
            viewModel.previousQuestion()
        }
        // обработка навигации из тулбара
        binding.toolbar.setNavigationOnClickListener {
            viewModel.isEnabledPreviousButton.value?.takeIf { it }
                ?.let { viewModel.previousQuestion() }
                ?: showToast(getString(R.string.info))
        }
        // установим обработчик
        binding.answerRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            (group.children.find { it.id == checkedId }?.tag as? Int)?.let {
                viewModel.setAnswer(it)
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun bindViewModel() {
        viewModel.isEnabledNextButton.observe(viewLifecycleOwner) {
            DebugHelper.log("QuizFragment|isEnabledNextButton: $it")
            binding.nextButton.isEnabled = it.first
            binding.nextButton.text = getString(it.second)
        }
        viewModel.isEnabledPreviousButton.observe(viewLifecycleOwner) {
            DebugHelper.log("QuizFragment|isEnabledPreviousButton: $it")
            binding.previousButton.isEnabled = it
        }
        viewModel.isShowQuestion.observe(viewLifecycleOwner) { question ->
            // установим титл
            binding.toolbar.title =
                getString(R.string.question_number, viewModel.titleN)
            DebugHelper.log("QuizFragment|isQuestion: ${binding.toolbar.title}")
            // очистим
            binding.answerRadioGroup.clearCheck()
            binding.answerRadioGroup.removeAllViews()
            // вопрос
            binding.questionTextView.text = question.question
            // заполняем ответы
            DebugHelper.log("QuizFragment|answerSelected: ${question.answerSelected}")
            question.answers.forEachIndexed { index, answer ->
                // создаем
                val radioButton = MaterialRadioButton(binding.answerRadioGroup.context)
                // сохраним номер ответа в тег
                radioButton.tag = index
                radioButton.text = answer.text
                // устанавливаем дизайн
                radioButton.setButtonDrawable(R.drawable.radiobutton_selector)
                radioButton.buttonTintList = radioButtonColorStateList
                // прикрепляем к родителю
                radioButton.layoutParams = radioGroupLayoutParams
                binding.answerRadioGroup.addView(radioButton)
            }
            // установим выбранный
            if (question.answerSelected in 0 until binding.answerRadioGroup.childCount) {
                (binding.answerRadioGroup.children.find {
                    it.tag == question.answerSelected
                } as? RadioButton)?.isChecked = true
            }
        }
        // показать в тулбаре кнопку назад
        viewModel.isEnabledPreviousButton.observe(viewLifecycleOwner) { it ->
            DebugHelper.log("isToolbarBack: $it")
            binding.toolbar.setNavigationIcon(
                if (it) R.drawable.ic_back_ios
                else R.drawable.ic_info
            )
        }
    }

    private fun showToast(messages: String) {
        Toast.makeText(context, messages, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
