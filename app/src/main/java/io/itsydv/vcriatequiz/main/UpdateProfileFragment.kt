package io.itsydv.vcriatequiz.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import io.itsydv.vcriatequiz.databinding.FragmentUpdateProfileBinding
import io.itsydv.vcriatequiz.splash.SplashActivity

class UpdateProfileFragment : Fragment() {
    private var _binding: FragmentUpdateProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        currentUser = auth.currentUser

        binding.ibBack.setOnClickListener {
            findNavController().navigate(UpdateProfileFragmentDirections.actionUpdateProfileFragmentToProfileFragment())
        }

        if (currentUser == null) {
            Toast.makeText(requireContext(), "You are not logged in", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), SplashActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        } else {
            binding.etName.setText(currentUser?.displayName)
            binding.etEmail.setText(currentUser?.email)

            binding.btnUpdateProfile.setOnClickListener {
                binding.btnUpdateProfile.isEnabled = false
                val name = binding.etName.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()

                if (name.isEmpty() || email.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show()
                    binding.btnUpdateProfile.isEnabled = true
                } else if (!email.contains("@") || !email.contains(".")) {
                    Toast.makeText(requireContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show()
                    binding.btnUpdateProfile.isEnabled = true
                } else {
                    var changesMade = false
                    if (name != currentUser!!.displayName) {
                        val profileUpdate = userProfileChangeRequest {
                            displayName = name
                        }
                        currentUser!!.updateProfile(profileUpdate)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    changesMade = true
                                    Toast.makeText(requireContext(), "Name updated successfully", Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(UpdateProfileFragmentDirections.actionUpdateProfileFragmentToProfileFragment())
                                }
                            }
                    }
                    if (email != currentUser!!.email) {
                        currentUser!!.updateEmail(email)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    changesMade = true
                                    Toast.makeText(requireContext(), "E-mail updated successfully", Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(UpdateProfileFragmentDirections.actionUpdateProfileFragmentToProfileFragment())
                                }
                            }
                    }
                    if (!changesMade) {
                        Toast.makeText(requireContext(), "No changes made", Toast.LENGTH_SHORT).show()
                        binding.btnUpdateProfile.isEnabled = true
                    }
                }
            }

            for (profile in currentUser!!.providerData) {
                if (profile.providerId == "password") {
                    binding.cvChangePassword.visibility = View.VISIBLE
                    binding.btnChangePassword.setOnClickListener {
                        val newPassword = binding.etPassword.text.toString()
                        currentUser!!.updatePassword(newPassword)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(UpdateProfileFragmentDirections.actionUpdateProfileFragmentToProfileFragment())
                                }
                            }
                    }
                } else {
                    binding.cvChangePassword.visibility = View.GONE
                }
            }

            binding.btnDeleteAccount.setOnClickListener {
                currentUser!!.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Account deleted", Toast.LENGTH_SHORT).show()
                            val intent = Intent(requireContext(), SplashActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}