package ru.ksart.quiz.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.google.android.material.radiobutton.MaterialRadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.ksart.quiz.R
import ru.ksart.quiz.databinding.FragmentQuizBinding
import ru.ksart.quiz.presentation.viewmodels.QuizViewModel
import ru.ksart.quiz.utils.DebugHelper

class QuizFragment : Fragment(R.layout.fragment_quiz) {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: QuizViewModel by activityViewModels()
    private var isToolbarNavigationBack = false

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
            if (isToolbarNavigationBack) {
                viewModel.previousQuestion()
            } else showToast(getString(R.string.info))
        }
        // установим обработчик
        binding.answerRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            binding.answerRadioGroup.findViewById<RadioButton>(checkedId)?.let {
                group.indexOfChild(it).takeIf { it >= 0 }?.let { checkedIndex ->
                    DebugHelper.log("QuizFragment|checkedIndex: ${checkedIndex}")
                    viewModel.setAnswer(checkedIndex)
                }
            }
        }
    }

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
            val selected = question.answerSelected
            DebugHelper.log("QuizFragment|answerSelected: ${selected}")
            question.answers.forEachIndexed { index, answer ->
                // создаем
                // com.google.android.material.radiobutton.MaterialRadioButton
                val radioButton = MaterialRadioButton(binding.answerRadioGroup.context)
                if (index == selected) {
                    DebugHelper.log("QuizFragment|answerSelected: ${selected}")
                    radioButton.isChecked = true
                }
                radioButton.text = answer.text
                binding.answerRadioGroup.addView(radioButton)
            }
        }
        // показать в тулбаре кнопку назад
        viewModel.isEnabledPreviousButton.observe(viewLifecycleOwner) {
            DebugHelper.log("isToolbarBack: $it")
            isToolbarNavigationBack = it
            binding.toolbar.setNavigationIcon(
                if (isToolbarNavigationBack) R.drawable.ic_back_ios
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