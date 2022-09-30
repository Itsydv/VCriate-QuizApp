package io.itsydv.vcriatequiz.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.itsydv.vcriatequiz.R
import io.itsydv.vcriatequiz.databinding.FragmentProfileBinding
import io.itsydv.vcriatequiz.splash.SplashActivity
import java.text.SimpleDateFormat
import java.util.*


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        currentUser = auth.currentUser

        binding.ibBack.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToFeedFragment())
        }

        if (currentUser == null) {
            Toast.makeText(requireContext(), "You are not logged in", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), SplashActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        } else {
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
            binding.tvUserName.text = currentUser!!.displayName?: "--"
            binding.tvEmail.text = currentUser!!.email

            val sfd = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            binding.tvAccountCreated.text = sfd.format(Date(currentUser!!.metadata!!.creationTimestamp))
            binding.llLogout.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes") { _, _ ->
                        auth.signOut()
                        val intent = Intent(requireContext(), SplashActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

            val score = requireActivity().getSharedPreferences("score", Context.MODE_PRIVATE)
                .getInt("score", -1)

            if (score != -1) {
                binding.btnStartQuiz.visibility = View.GONE
                binding.tvHeading2.text = getString(R.string.your_score)
                binding.tvScore.visibility = View.VISIBLE
                binding.tvScore.text = score.toString()
            } else {
                binding.tvHeading2.text = getString(R.string.quiz_not_taken)
                binding.tvScore.visibility = View.GONE
                binding.btnStartQuiz.visibility = View.VISIBLE
                binding.btnStartQuiz.setOnClickListener {
                    findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToQuestionFragment())
                }
            }

            binding.tvEditProfile.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToUpdateProfileFragment())
            }

            binding.tvLogout.setOnClickListener {
                binding.llLogout.performClick()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}