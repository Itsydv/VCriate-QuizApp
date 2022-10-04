package io.itsydv.vcriatequiz.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.itsydv.vcriatequiz.R
import io.itsydv.vcriatequiz.databinding.FragmentLoginBinding
import io.itsydv.vcriatequiz.main.MainActivity

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.btnLogin.setOnClickListener {
            binding.btnLogin.isEnabled = false
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show()
                binding.btnLogin.isEnabled = true
            } else if (!email.contains("@") || !email.contains(".")) {
                Toast.makeText(requireContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show()
                binding.btnLogin.isEnabled = true
            } else {
                signIn(email, password)
            }
        }

        binding.btnGoogleLogin.setOnClickListener {
            Toast.makeText(requireContext(), "Logging In", Toast.LENGTH_SHORT).show()
            googleSignIn()
        }

        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth

        resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    if (account != null) {
                        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                        auth.signInWithCredential(credential)
                            .addOnCompleteListener { firebaseTask ->
                                if (firebaseTask.isSuccessful) {
                                    val intent = Intent(requireActivity(), MainActivity::class.java)
                                    startActivity(intent)
                                    requireActivity().finish()
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(requireContext(), "Authentication Failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Authentication Failed", Toast.LENGTH_SHORT).show()
                                it.printStackTrace()
                            }
                    }
                } catch (e: ApiException) {
                    Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        super.onCreate(savedInstanceState)
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    // If sign in fails, display a message to the user.
                    when(task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            Toast.makeText(requireContext(), "Invalid Credentials, Try again", Toast.LENGTH_SHORT).show()
                        }
                        is FirebaseAuthInvalidUserException -> {
                            Toast.makeText(requireContext(), "Invalid User", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
                        }
                        else -> {
                            Toast.makeText(requireContext(),
                                "Authentication failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                    binding.etEmail.setText("")
                    binding.etPassword.setText("")
                    binding.btnLogin.isEnabled = true
                }
            }
    }

    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}