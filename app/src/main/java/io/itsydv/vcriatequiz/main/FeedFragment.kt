package io.itsydv.vcriatequiz.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

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
            binding.tvUserName.text = currentUser!!.displayName ?: "Name not set"
            if (currentUser?.photoUrl != null) {
                binding.ivProfilePic.load(currentUser?.photoUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_user_img)
                }
            } else {
                binding.ivProfilePic.load(Gravatar.getProfileUrl(currentUser!!.email!!)) {
                    crossfade(true)
                    placeholder(R.drawable.ic_user_img)
                }
            }
            binding.cvProfilePic.setOnClickListener {
                findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToProfileFragment())
            }
        }

        model.questions.observe(viewLifecycleOwner) { questions ->
            when(questions) {
                is Resource.Loading -> {
                    binding.btnStartQuiz.visibility = View.GONE
                    binding.pbLoading.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), questions.message, Toast.LENGTH_SHORT).show()
                    binding.btnStartQuiz.visibility = View.GONE
                    binding.pbLoading.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.pbLoading.visibility = View.GONE
                    val result = questions.data!!.body()!!.result
                    binding.btnStartQuiz.apply {
                        visibility = View.VISIBLE
                        setOnClickListener {

                            if (requireActivity().getSharedPreferences("score", Context.MODE_PRIVATE)
                                    .getInt("score", -1) != -1) {
                                Toast.makeText(requireContext(), "You have already taken the quiz, check Results here", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToProfileFragment())
                            } else {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Start Quiz")
                                    .setMessage("Are you sure you want to start the quiz?\nTotal questions: ${result.questions.size}\nTime limit: ${result.timeInMinutes} minutes")
                                    .setPositiveButton("Yes") { _, _ ->
                                        findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToQuestionFragment())
                                    }
                                    .setNegativeButton("No") { _, _ -> }
                                    .show()
                            }
                        }
                    }
                }
            }
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
        super.onDestroyView()
        _binding = null
    }
}