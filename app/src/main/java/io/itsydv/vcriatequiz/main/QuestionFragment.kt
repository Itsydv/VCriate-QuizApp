package io.itsydv.vcriatequiz.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.itsydv.vcriatequiz.R
import io.itsydv.vcriatequiz.databinding.FragmentQuestionBinding
import io.itsydv.vcriatequiz.databinding.ItemOptionBinding
import io.itsydv.vcriatequiz.models.Question

class QuestionFragment : Fragment() {

    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!

    private val feedModel by activityViewModels<FeedViewModel>()
    private val questionModel by activityViewModels<QuestionViewModel>()
    private var selectedOptions = mutableListOf<Int>()
    private lateinit var timer: CountDownTimer
    private var score = 0

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

                val quizTime = it.data.body()!!.result.timeInMinutes
                timer = object : CountDownTimer((quizTime*60*1000).toLong(), 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val minutes = millisUntilFinished / 1000 / 60
                        val seconds = millisUntilFinished / 1000 % 60
                        binding.tvTimeRemaining.text = getString(R.string.time_left, minutes, seconds)
                    }
                    override fun onFinish() {
                        binding.tvTimeRemaining.text = getString(R.string.time_up)
                        showResult(totalQuestions)
                    }
                }.start()

                questionModel.questionNumber.observe(viewLifecycleOwner) { qNum ->
                    val question = questions[qNum]
                    binding.lpiProgress.progress = qNum + 1
                    updateQuestion(question)

                    binding.tvQuestionNumber.text = getString(
                        R.string.question_progress,
                        if ((qNum + 1).toString().length > 1) (qNum + 1).toString() else "0${(qNum + 1)}",
                        if (totalQuestions.toString().length > 1) totalQuestions.toString() else "0$totalQuestions"
                    )

                    binding.btnNext.text = if (qNum == totalQuestions-1) "Submit" else getString(R.string.next)
                    binding.btnNext.setOnClickListener {
                        if (qNum == totalQuestions-1) {
                            timer.cancel()
                            evaluateQuizResult(questions[qNum].correct_answers)
                            showResult(totalQuestions)
                        } else {
                            if (selectedOptions.size > 0) {
                                evaluateQuizResult(questions[qNum].correct_answers)
                                resetOptions()
                            }
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

    private fun showResult(totalQuestions: Int) {
        binding.clContent.visibility = View.GONE
        binding.clControls.visibility = View.GONE
        binding.quizResult.apply {
            clResults.visibility = View.VISIBLE
            if (score == totalQuestions) {
                tvResult.text = "Congratulations!"
            } else {
                tvResult.text = "Better luck next time!"
            }
            tvScore.text = "$score/$totalQuestions"
            requireActivity().getSharedPreferences("score", Context.MODE_PRIVATE).edit().putInt("score", score).apply()

            btnShareResults.setOnClickListener {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_result, score, totalQuestions))
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }

            btnReturnToFeed.setOnClickListener {
                findNavController().navigate(QuestionFragmentDirections.actionQuestionFragmentToFeedFragment())
            }
        }
    }

    private fun updateQuestion(question: Question) {
        binding.tvQuestion.text = question.lable
        binding.option1.tvOption.text = question.options[0].lable
        binding.option2.tvOption.text = question.options[1].lable
        binding.option3.tvOption.text = question.options[2].lable
        binding.option4.tvOption.text = question.options[3].lable

        binding.option1.llOption.setOnClickListener {
            selectOption(binding.option1, 1)
        }
        binding.option2.llOption.setOnClickListener {
            selectOption(binding.option2, 2)
        }
        binding.option3.llOption.setOnClickListener {
            selectOption(binding.option3, 3)
        }
        binding.option4.llOption.setOnClickListener {
            selectOption(binding.option4, 4)
        }
    }

    private fun selectOption(view: ItemOptionBinding, option: Int) {
        if (selectedOptions.contains(option)) {
            selectedOptions.remove(option)
            view.llOption.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.shimmerDark)
        } else {
            selectedOptions.add(option)
            view.llOption.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.success)
        }
    }

    private fun evaluateQuizResult(answers: List<Int>) {
        if (selectedOptions == answers ) {
            score++
        }
    }

    private fun resetOptions() {
        selectedOptions.clear()
        binding.option1.llOption.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.shimmerDark)
        binding.option2.llOption.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.shimmerDark)
        binding.option3.llOption.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.shimmerDark)
        binding.option4.llOption.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.shimmerDark)
    }

    override fun onDestroyView() {
        timer.cancel()
        super.onDestroyView()
        _binding = null
    }
}