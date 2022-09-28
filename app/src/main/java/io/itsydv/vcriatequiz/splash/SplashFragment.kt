package io.itsydv.vcriatequiz.splash

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.itsydv.vcriatequiz.R
import io.itsydv.vcriatequiz.databinding.FragmentSplashBinding
import io.itsydv.vcriatequiz.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment(){

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

//    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        auth = Firebase.auth
//        val currentUser = auth.currentUser

        binding.ivLogo.load(R.drawable.ic_logo) {
            crossfade(true)
            crossfade(1000)
        }

        lifecycleScope.launch {
            val tvAppName = binding.tvAppName
            val delayTime = 50L
            delay(300)
            tvAppName.text = "Q"
            delay(delayTime)
            tvAppName.append("u")
            delay(delayTime)
            tvAppName.append("i")
            delay(delayTime)
            tvAppName.append("z")
            delay(delayTime)
            tvAppName.append("A")
            delay(delayTime)
            tvAppName.append("p")
            delay(delayTime)
            tvAppName.append("p")
            delay(300)
            binding.pbLoading.visibility = View.VISIBLE
            delay(500)

            requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primaryColor)

            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()

//            if (currentUser == null) {
//                // TODO: navigate to login fragment
//            } else {
//                val intent = Intent(requireContext(), MainActivity::class.java)
//                startActivity(intent)
//                requireActivity().finish()
//            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}