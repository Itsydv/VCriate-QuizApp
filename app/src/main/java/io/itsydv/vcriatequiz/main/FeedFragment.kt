package io.itsydv.vcriatequiz.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.itsydv.vcriatequiz.databinding.FragmentFeedBinding
import java.util.*

class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val model: FeedViewModel by activityViewModels()

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

         binding.tvGreet.text = greet()
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