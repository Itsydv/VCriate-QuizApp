package io.itsydv.vcriatequiz.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.itsydv.vcriatequiz.databinding.FragmentQuestionBinding

class QuestionFragment : Fragment() {

    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<QuestionFragmentArgs>()
    private val model by activityViewModels<FeedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val questionNumber = args.questionNumber

        model.questions.observe(viewLifecycleOwner) {

            if (it.data != null) {
                val question = it.data.body()!!.result.questions[questionNumber]
                binding.tvQuestion.text = question.lable
                binding.option1.tvOption.text = question.options[0].lable
                binding.option2.tvOption.text = question.options[1].lable
                binding.option3.tvOption.text = question.options[2].lable
                binding.option4.tvOption.text = question.options[3].lable
            } else {
                binding.ibBack.performClick()
            }
        }

        binding.ibBack.setOnClickListener {
            findNavController().navigate(QuestionFragmentDirections.actionQuestionFragmentToFeedFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}