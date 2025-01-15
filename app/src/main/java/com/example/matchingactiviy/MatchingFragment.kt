package com.example.matchingactiviy

import android.annotation.SuppressLint
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
import com.example.matchingactiviy.databinding.FragmentMatchingBinding

class MatchingFragment : Fragment() {
    private lateinit var binding: FragmentMatchingBinding
    private lateinit var lineView: LineView
  //  private var tts: TextToSpeech? = null
    private val chunkSize = 6

    private val wordMeaningPairs = listOf(
        "Apple" to "A fruit that keeps the doctor away",
        "Book" to "A collection of written, printed, or blank pages",
        "Car" to "A vehicle with four wheels powered by an engine",
        "Dog" to "A domesticated mammal, often kept as a pet",
        "Earth" to "The planet we live on",
        "Fish" to "An aquatic animal with fins and gills",
        "Sun" to "The star at the center of our solar system",
        "Moon" to "Earth's natural satellite",
        "Tree" to "A large plant with a trunk and branches",
        "House" to "A building for human habitation",
        "Cat" to "A small domesticated carnivorous mammal",
        "Bird" to "A warm-blooded vertebrate animal with feathers",
        "Water" to "A colorless, transparent, odorless liquid",
        "Fire" to "Rapid oxidation of a material in the exothermic chemical process of combustion",
        "Wind" to "The perceptible natural movement of the air",
        "Flower" to "The seed-bearing part of a plant",
        "Grass" to "Vegetation consisting of typically short green plants",
        "Stone" to "Solid nonmetallic mineral matter",
        "Mountain" to "A large natural elevation of the earth's surface",
        "River" to "A large natural stream of water flowing in a channel to the sea, a lake, or another river",
        "Ocean" to "A very large expanse of sea",
        "Cloud" to "A visible mass of condensed water vapor floating in the atmosphere",
        "Rain" to "Water falling from the atmosphere in visible drops",
        "Snow" to "Atmospheric water vapor frozen into ice crystals and falling in light white flakes",
        "Star" to "A fixed luminous point in the night sky",
        "Sky" to "The region of the atmosphere and outer space seen from the earth",
        "Time" to "The indefinite continued progress of existence and events that occur in apparently irreversible succession from the past, through the present, to the future",
        "Life" to "The condition that distinguishes organisms from inorganic matter",
        "Death" to "The permanent cessation of all vital functions",
        "Love" to "An intense feeling of deep affection",
        "Hate" to "Intense or passionate dislike",
        "Fear" to "An unpleasant emotion caused by the perception of danger, real or imagined",
        "Joy" to "A feeling of great pleasure and happiness",
        "Pain" to "Physical suffering or discomfort caused by illness or injury",
        "Health" to "The state of being free from illness or injury",
        "Money" to "A current medium of exchange in the form of coins and banknotes",
        "Food" to "Any nutritious substance that people or animals eat in order to maintain life and growth",
        "Drink" to "Liquid suitable for swallowing",
        "Sleep" to "A condition of body and mind such as that which typically recurs for several hours every night, in which the nervous system is relatively inactive, the eyes closed, the postural muscles relaxed, and consciousness practically suspended",
        "Work" to "Activity involving mental or physical effort done in order to achieve a purpose or result",
        "Play" to "Engage in activity for enjoyment and recreation rather than a serious or practical purpose",
        "Learn" to "Gain or acquire knowledge of or skill in (something) by study, experience, or being taught",
        "Teach" to "Show or explain to (someone) how to do something",
        "Think" to "Have a particular belief or idea",
        "Feel" to "Be aware of (a sensation or an emotion)",
        "See" to "Perceive with the eyes; discern visually",
        "Hear" to "Perceive with the ear the sound made by (someone or something)",
        "Speak" to "Say something in order to convey information, an opinion, or a feeling",
        "Write" to "Mark (letters, words, or other symbols) on a surface, typically paper, with a pen, pencil, or similar implement",
        "Read" to "Look at and comprehend the meaning of (written or printed matter) by interpreting the characters or symbols of which it is composed",
        "Walk" to "Move at a regular pace by lifting and setting down each foot alternately",
        "Run" to "Move at a speed faster than a walk, never having both or all the feet on the ground at the same time",
        "Jump" to "Push oneself off a surface and into the air by using the muscles in one's legs and feet",
        "Swim" to "Propel oneself through water using the limbs or (in the case of a fish) fins",
        "Fly" to "Move through the air using wings",
        "Drive" to "Operate and control the direction and speed of a motor vehicle",
        "Build" to "Construct (something, typically a building, road, or machine) by putting parts or materials together over a period of time",
        "Destroy" to "Put an end to the existence of (something) by damaging or attacking it irreparably",
        "Create" to "Bring (something) into existence",
        "Change" to "Make or become different",
        "Keep" to "Retain possession of",
        "Give" to "Freely transfer possession of (something) to (someone)",
        "Take" to "Lay hold of (something) with one's hands; reach for and grasp",
        "Use" to "Take, hold, or deploy (something) as a means of accomplishing a purpose or achieving a result; employ",
        "Find" to "Discover (something or someone) unexpectedly or while looking or searching for something else"

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
            requireActivity().finish()
//            requireActivity().supportFragmentManager.popBackStack()
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
        val textViews = listOf(binding.text1, binding.text2, binding.text3, binding.text4, binding.text5, binding.text6)
        textViews.forEachIndexed { index, textView ->
            textView.text = words.getOrNull(index) ?: ""
            textView.contentDescription = words.getOrNull(index) ?: ""
        }

    }

    private fun setMeanings(meanings: List<String>) {
        val meaningViews = listOf(binding.meaning1, binding.meaning2, binding.meaning3, binding.meaning4, binding.meaning5, binding.meaning6)
        meaningViews.forEachIndexed { index, meaningView ->
            meaningView.text = meanings.getOrNull(index) ?: ""
            meaningView.contentDescription = meanings.getOrNull(index) ?: ""
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun textTouchListeners() {
        val textViews = listOf(binding.text1, binding.text2, binding.text3, binding.text4, binding.text5, binding.text6)
        textViews.forEach { textView ->
            textView.setOnTouchListener { _, event -> handleTouch(event, textView) }
        }

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
        for (meaningView in listOf(binding.meaning1, binding.meaning2, binding.meaning3, binding.meaning4, binding.meaning5, binding.meaning6)) {
            meaningView.getHitRect(hitRect)
            if (hitRect.contains(x.toInt(), y.toInt())) {
                if (meaningView.text.toString() == correctMeaning) {
                    // Correct match
                    textView.isEnabled = false
                    lineView.endCoordinates = getCenterCoordinates(meaningView)
                    val line = LineView.LineList(lineView.startCoordinates, lineView.endCoordinates)
                    lineView.lineList.add(line)

                    if (lineView.lineList.size == minOf(6, currentPairs.size)) {
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
            resetViewState()
            initializeGame()
        } else {
            Toast.makeText(requireContext(), "Finish", Toast.LENGTH_SHORT).show()
        }
    }
    private fun resetViewState() {
        // Reset text views
        val textViews = listOf(binding.text1, binding.text2, binding.text3, binding.text4, binding.text5, binding.text6)
        textViews.forEach { textView ->
            textView.isEnabled = true  // Re-enable all text views
        }

        // Clear all lines
        lineView.lineList.clear()
        lineView.startCoordinates = null
        lineView.endCoordinates = null
        lineView.invalidate()  // Force redraw of the line view
        binding.imgNext.visibility = View.GONE

    }
    private fun getCenterCoordinates(view: View): Pair<Float, Float> {
        val x = view.x + view.width / 2
        val y = view.y + view.height / 2
        return Pair(x, y)
    }

    override fun onDestroy() {
        super.onDestroy()
//        tts?.shutdown()
    }
}