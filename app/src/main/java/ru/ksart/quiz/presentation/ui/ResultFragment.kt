package ru.ksart.quiz.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.ksart.quiz.R
import ru.ksart.quiz.databinding.FragmentResultBinding
import ru.ksart.quiz.presentation.viewmodels.QuizViewModel
import ru.ksart.quiz.utils.DebugHelper

class ResultFragment : Fragment(R.layout.fragment_result) {
    private var _binding: FragmentResultBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: QuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DebugHelper.log("ResultFragment|onViewCreated")
        bindViewModel()
        initListener()
        viewModel.showResult()
    }

    private fun bindViewModel() {
        viewModel.isShowResult.observe(viewLifecycleOwner) {
            binding.resultTextView.text =
                getString(R.string.result_text, it.first, it.second, it.third)
        }
    }

    private fun initListener() {
        binding.share.setOnClickListener {
            viewModel.shareResult()
        }
        binding.restart.setOnClickListener {
            viewModel.restartQuestion()
        }
        binding.close.setOnClickListener {
            viewModel.exit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
