package pl.ozodbek.findword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import pl.ozodbek.findword.databinding.FragmentGameEntryBinding

class GameEntry : Fragment() {
    private var _binding: FragmentGameEntryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGameEntryBinding.inflate(inflater, container, false)

        binding.startButton.setOnClickListener {
            findNavController(binding.root).navigate(R.id.gameProcess)
        }

        return binding.root

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}