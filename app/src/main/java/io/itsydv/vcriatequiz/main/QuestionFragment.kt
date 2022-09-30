package io.itsydv.vcriatequiz.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.itsydv.vcriatequiz.R
import io.itsydv.vcriatequiz.databinding.FragmentQuestionBinding
import io.itsydv.vcriatequiz.models.Question

class QuestionFragment : Fragment() {

    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!

    private val feedModel by activityViewModels<FeedViewModel>()
    private val questionModel by activityViewModels<QuestionViewModel>()
    private var attempted = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        questionModel.questionNumber.postValue(0)

        feedModel.questions.observe(viewLifecycleOwner) {
            if (it.data != null) {
                val questions = it.data.body()!!.result.questions
                val totalQuestions = questions.size
                binding.lpiProgress.max = totalQuestions

                questionModel.questionNumber.observe(viewLifecycleOwner) { qNum ->
                    val question = questions[qNum]
                    binding.lpiProgress.progress = qNum + 1
                    updateQuestion(question)

                    binding.tvQuestionNumber.text = getString(
                        R.string.question_progress,
                        if ((qNum + 1).toString().length > 1) (qNum + 1).toString() else "0${(qNum + 1)}",
                        if (totalQuestions.toString().length > 1) totalQuestions.toString() else "0$totalQuestions"
                    )

                    binding.btnNext.text = if (qNum == totalQuestions-1) "Submit" else if (attempted) "Next" else "Skip"
                    binding.btnNext.setOnClickListener {
                        if (qNum == totalQuestions-1) {
                            evaluateQuizResult()
                        } else {
                            questionModel.questionNumber.postValue(qNum + 1)
                        }
                    }
                }

            } else {
                binding.ibBack.performClick()
            }
        }

        binding.ibBack.setOnClickListener {
            findNavController().navigate(QuestionFragmentDirections.actionQuestionFragmentToFeedFragment())
        }

        binding.btnQuit.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Are you sure?")
                .setMessage("You will lose your progress")
                .setPositiveButton("Yes") { _, _ ->
                    findNavController().navigate(QuestionFragmentDirections.actionQuestionFragmentToFeedFragment())
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun updateQuestion(question: Question) {
        binding.tvQuestion.text = question.lable
        binding.option1.tvOption.text = question.options[0].lable
        binding.option2.tvOption.text = question.options[1].lable
        binding.option3.tvOption.text = question.options[2].lable
        binding.option4.tvOption.text = question.options[3].lable
    }

    private fun evaluateQuizResult() {
        Toast.makeText(requireContext(), "Quiz submitted", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}