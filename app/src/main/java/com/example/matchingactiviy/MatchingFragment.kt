package com.example.matchingactiviy


//*****************النص الثاني **********************

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import coil.load
import com.example.matchingactiviy.databinding.FragmentMatchingBinding
class MatchingFragment : Fragment() {
    private lateinit var binding: FragmentMatchingBinding
    private lateinit var lineView: LineView
    private var tts: TextToSpeech? = null
    private val chunkSize = 3

    private val wordMeaningPairs = listOf(
        "Apple" to "A fruit that keeps the doctor away",
        "Book" to "A collection of written, printed, or blank pages",
        "Car" to "A vehicle with four wheels powered by an engine",
        "Dog" to "A domesticated mammal, often kept as a pet",
        "Earth" to "The planet we live on",
        "Fish" to "An aquatic animal with fins and gills"
    )

    private var currentPairs = listOf<Pair<String, String>>()
    private var currentListChunkPos = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMatchingBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lineView = binding.lineview
        initializeGame()

        binding.imgClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.imgNext.setOnClickListener {
            moveToNextSet()
        }

        textTouchListeners()
    }

    private fun initializeGame() {
        val startIdx = currentListChunkPos * chunkSize
        currentPairs = wordMeaningPairs.slice(startIdx until minOf(startIdx + chunkSize, wordMeaningPairs.size))

        val words = currentPairs.map { it.first }
        val meanings = currentPairs.map { it.second }.shuffled()

        setWords(words)
        setMeanings(meanings)

        lineView.lineList.clear()
    }

    private fun setWords(words: List<String>) {
        binding.text1.text = words.getOrNull(0) ?: ""
        binding.text2.text = words.getOrNull(1) ?: ""
        binding.text3.text = words.getOrNull(2) ?: ""

        binding.text1.contentDescription = words.getOrNull(0) ?: ""
        binding.text2.contentDescription = words.getOrNull(1) ?: ""
        binding.text3.contentDescription = words.getOrNull(2) ?: ""
    }

    private fun setMeanings(meanings: List<String>) {
        binding.meaning1.text = meanings.getOrNull(0) ?: ""
        binding.meaning2.text = meanings.getOrNull(1) ?: ""
        binding.meaning3.text = meanings.getOrNull(2) ?: ""

        binding.meaning1.contentDescription = meanings.getOrNull(0) ?: ""
        binding.meaning2.contentDescription = meanings.getOrNull(1) ?: ""
        binding.meaning3.contentDescription = meanings.getOrNull(2) ?: ""
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun textTouchListeners() {
        binding.text1.setOnTouchListener { _, event -> handleTouch(event, binding.text1) }
        binding.text2.setOnTouchListener { _, event -> handleTouch(event, binding.text2) }
        binding.text3.setOnTouchListener { _, event -> handleTouch(event, binding.text3) }
    }

    private fun handleTouch(event: MotionEvent, textView: TextView): Boolean {
        val coordinates = getCenterCoordinates(textView)
        val startX = coordinates.first
        val startY = coordinates.second

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lineView.startCoordinates = coordinates
                lineView.endCoordinates = null
                lineView.invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                lineView.endCoordinates = Pair(event.x + startX, event.y + startY)
                lineView.invalidate()
            }
            MotionEvent.ACTION_UP -> {
                val endX = lineView.endCoordinates?.first!!
                val endY = lineView.endCoordinates?.second!!
                endLineDraw(textView, endX, endY)
                lineView.invalidate()
            }
        }
        return true
    }

    private fun endLineDraw(textView: TextView, x: Float, y: Float) {
        val word = textView.text.toString()
        val correctMeaning = wordMeaningPairs.find { it.first == word }?.second

        val hitRect = Rect()
        for (meaningView in listOf(binding.meaning1, binding.meaning2, binding.meaning3)) {
            meaningView.getHitRect(hitRect)
            if (hitRect.contains(x.toInt(), y.toInt())) {
                if (meaningView.text.toString() == correctMeaning) {
                    // Correct match
                    textView.isEnabled = false
                    lineView.endCoordinates = getCenterCoordinates(meaningView)
                    val line = LineView.LineList(lineView.startCoordinates, lineView.endCoordinates)
                    lineView.lineList.add(line)

                    if (lineView.lineList.size == minOf(3, currentPairs.size)) {
                        binding.imgNext.visibility = View.VISIBLE
                    }
                } else {
                    // Wrong match
                    meaningView.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.shake))
                    lineView.endCoordinates = null
                }
                break
            }
        }
    }

    private fun moveToNextSet() {
        if (wordMeaningPairs.size > (currentListChunkPos + 1) * chunkSize) {
            currentListChunkPos++
            binding.imgNext.visibility = View.GONE
            initializeGame()
        } else {
            Toast.makeText(requireContext(), "Finish", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCenterCoordinates(view: View): Pair<Float, Float> {
        val x = view.x + view.width / 2
        val y = view.y + view.height / 2
        return Pair(x, y)
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.shutdown()
    }
}