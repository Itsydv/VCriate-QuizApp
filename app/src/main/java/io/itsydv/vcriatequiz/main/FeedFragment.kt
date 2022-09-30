package io.itsydv.vcriatequiz.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.itsydv.vcriatequiz.R
import io.itsydv.vcriatequiz.databinding.FragmentFeedBinding
import io.itsydv.vcriatequiz.splash.SplashActivity
import java.util.*

class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val model: FeedViewModel by activityViewModels()
    private lateinit var allQuestionsAdapter: AllQuestionsAdapter

    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

//    private lateinit var listener: ValueEventListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(requireContext(), "You are not logged in", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), SplashActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        } else {
            binding.tvGreet.text = greet()
            binding.tvUserName.text = currentUser!!.displayName
            binding.ivProfilePic.load(Gravatar.getProfileUrl(currentUser!!.email!!)) {
                crossfade(true)
                placeholder(R.drawable.ic_user_img)
            }
        }

        setupRecyclerView()

        model.questions.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Loading -> {
                    binding.rvQuestions.visibility = View.GONE
                    binding.shimmerViewContainer.visibility = View.VISIBLE
                    binding.shimmerViewContainer.startShimmer()
                }
                is Resource.Error -> {
                    binding.rvQuestions.visibility = View.GONE
                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility = View.GONE
                    binding.rvQuestions.visibility = View.VISIBLE
                    val questions = it.data!!.body()!!.result.questions
                    binding.tvNumOfQuestions.text = getString(R.string.num_of_questions, questions.size)
                    allQuestionsAdapter.differ.submitList(questions)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        allQuestionsAdapter = AllQuestionsAdapter{
            findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToQuestionFragment(it))
        }
        binding.rvQuestions.apply {
            adapter = allQuestionsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun greet(): String {
        val rightNow = Calendar.getInstance()

        return when (rightNow.get(Calendar.HOUR_OF_DAY)) {
            in 12..15 -> {
                "Good Afternoon!"
            }
            in 16..20 -> {
                "Good Evening!"
            }
            in 21..24, in 0..4 -> {
                "Good Night!"
            }
            else -> {
                "Good Morning!"
            }
        }
    }

    override fun onDestroyView() {
//        if (this::listener.isInitialized) {
//            database.removeEventListener(listener)
//        }
        super.onDestroyView()
        _binding = null
    }
}