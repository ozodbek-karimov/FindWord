package pl.ozodbek.findword

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout

import androidx.navigation.fragment.findNavController
import pl.ozodbek.findword.databinding.FragmentGameProcessBinding

class GameProcess : Fragment() {

    private var _binding: FragmentGameProcessBinding? = null
    private val binding get() = _binding!!

    private lateinit var answerButtons: ArrayList<Button>
    private var helpButtonClicked = false
    private var currentCorrectLetterIndex = 0
    private val answerText = "Helicopter"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameProcessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        answerButtons = ArrayList()

        // set up the images
        binding.firstImage.setImageResource(R.drawable.img)
        binding.secondImage.setImageResource(R.drawable.img_1)
        binding.thirdImage.setImageResource(R.drawable.img_2)
        binding.fourthImage.setImageResource(R.drawable.img_3)

        setUpAnswerButtons()
        setUpGuessButtons()


        binding.shuffleButton.setOnClickListener {
            val guessedText = answerText.toCharArray().toList().shuffled()
            for (i in guessedText.indices) {
                val button = binding.guessLayout.getChildAt(i) as Button
                button.text = guessedText[i].toString()
                button.visibility = View.VISIBLE
                answerButtons[i].text = ""
                answerButtons[i].visibility = View.INVISIBLE
            }
        }

        binding.helpButton.setOnClickListener {
            helpButtonClicked = true
            showHelpLetter()
        }
    }
    private fun setUpAnswerButtons() {
        binding.answerLayout.columnCount = answerText.length
        for (i in answerText.indices) {
            val button = Button(requireContext())
            button.text = ""
            button.visibility = View.INVISIBLE
            val params = GridLayout.LayoutParams()
            params.setMargins(5, 5, 5, 5)
            params.width = 0
            params.height = GridLayout.LayoutParams.WRAP_CONTENT
            params.columnSpec = GridLayout.spec(i, 1f / answerText.length)
            binding.answerLayout.addView(button, params)
            button.setOnClickListener { removeLetterFromAnswer(button) }
            answerButtons.add(button)
        }
    }

    private fun setUpGuessButtons() {
        val guessText = answerText.toCharArray().toList().shuffled()
        val guessLayoutRowCount = if (guessText.size > 5) 2 else 1
        binding.guessLayout.rowCount = guessLayoutRowCount
        binding.guessLayout.columnCount =
            if (guessLayoutRowCount == 1) guessText.size else guessText.size / 2 + 1
        for (i in guessText.indices) {
            val button = Button(requireContext())
            button.text = guessText[i].toString()
            button.setOnClickListener { addLetterToAnswer(button) }
            val params = GridLayout.LayoutParams()
            params.setMargins(5, 5, 5, 5)
            params.width = 0
            params.height = GridLayout.LayoutParams.WRAP_CONTENT
            params.setGravity(Gravity.CENTER)
            params.columnSpec =
                GridLayout.spec(
                    i % (guessText.size / guessLayoutRowCount),
                    1f / (guessText.size / guessLayoutRowCount)
                )
            params.rowSpec = GridLayout.spec(
                i / binding.guessLayout.columnCount,
                1f / guessLayoutRowCount
            )
            binding.guessLayout.addView(button, params)
        }
    }



    private fun removeLetterFromAnswer(button: Button) {
        Log.d("GameProcess", "Removing letter from button ${answerButtons.indexOf(button)}")
        // remove the text from the button and make it visible in the guess layout
        val index = answerButtons.indexOf(button)
        if (index != -1 && button.visibility == View.VISIBLE) {
            // check that index is within bounds of answerButtons
            if (index < answerButtons.size) {
                button.visibility = View.INVISIBLE
                val guessLayout = binding.guessLayout
                for (i in 0 until guessLayout.childCount) {
                    val guessButton = guessLayout.getChildAt(i) as Button
                    if (guessButton.text == button.text) {
                        guessButton.visibility = View.VISIBLE
                        break
                    }
                }
                answerButtons[index].text = ""
            } else {
                Log.e("GameProcess", "Index out of bounds: $index")
            }
        }
    }

    private fun addLetterToAnswer(button: Button) {
        // add the text from the button to the answer layout and make it invisible in the guess layout
        val index = answerButtons.indexOfFirst { it.text.isEmpty() }
        if (index != -1) {
            if (button.visibility == View.INVISIBLE) {
                return
            }
            val alreadyInAnswer = answerButtons.any { it.text == button.text }
            if (!alreadyInAnswer) {
                val guessLayout = binding.guessLayout
                val guessIndex = (0 until guessLayout.childCount)
                    .firstOrNull { guessLayout.getChildAt(it).visibility == View.VISIBLE }
                if (guessIndex != null) {
                    val guessButton = guessLayout.getChildAt(guessIndex) as Button
                    guessButton.visibility = View.INVISIBLE
                    answerButtons[index].text = guessButton.text
                    answerButtons[index].visibility = View.VISIBLE
                    button.visibility = View.VISIBLE
                }
            }
        }
        winCheckFunction()
    }


    private fun winCheckFunction() {
        if (answerButtons.equals(answerText)) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Congratulations!")
            builder.setMessage("You have correctly guessed the word 'Helicopter'.")
            builder.setPositiveButton("Play again") { dialog, _ ->

                val guessedText = answerText.toCharArray().toList().shuffled()
                for (i in guessedText.indices) {
                    val button = binding.guessLayout.getChildAt(i) as Button
                    button.text = guessedText[i].toString()
                    button.visibility = View.VISIBLE
                    answerButtons[i].text = ""
                    answerButtons[i].visibility = View.INVISIBLE
                }
                dialog.dismiss()
            }
            builder.setNegativeButton("Quit") { _, _ ->
                findNavController().navigate(R.id.gameEntry)
            }
            builder.setCancelable(false)
            builder.show()
        }
    }

    private fun showHelpLetter() {
        // show each letter in the answer layout
        for (i in answerText.indices) {
            val answerButton = answerButtons[i]
            if (answerButton.text.isEmpty()) {
                answerButton.text = answerText[i].toString()
                answerButton.setBackgroundColor(Color.GREEN)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
