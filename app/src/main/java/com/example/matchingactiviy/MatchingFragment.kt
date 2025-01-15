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
//*****************النص الاول **********************
//import android.annotation.SuppressLint
//import android.graphics.Color
//import android.graphics.Rect
//import android.os.Bundle
//import android.speech.tts.TextToSpeech
//import android.view.LayoutInflater
//import android.view.MotionEvent
//import android.view.View
//import android.view.ViewGroup
//import android.view.animation.AnimationUtils
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import coil.load
//import com.example.matchingactiviy.databinding.FragmentMatchingBinding
//
//class MatchingFragment : Fragment() {
//
//    private lateinit var binding: FragmentMatchingBinding
//    private lateinit var lineView: LineView
//    private var tts: TextToSpeech? = null
//    private val chunkSize = 3
//    private val chunked = Constant.LettersList.chunked(chunkSize)
//    private var letterList = listOf<String>()
//    private var picList = listOf<String>()
//    private var currentListChunkPos = 0
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentMatchingBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        lineView = binding.lineview
//        initializeGame()
//
//        binding.imgClose.setOnClickListener {
//            requireActivity().supportFragmentManager.popBackStack()
//        }
//
//        binding.imgNext.setOnClickListener {
//            moveToNextSet()
//        }
//
//        textTouchListeners()  // تم تعديلها لتكون خاصة بالنصوص
//    }
//    private val wordMeaningPairs = listOf(
//        "Apple" to "A fruit that keeps the doctor away",
//        "Book" to "A collection of written, printed, or blank pages",
//        "Car" to "A vehicle with four wheels powered by an engine"
//    )
//
////    private lateinit var letterList: List<String>
////    private lateinit var picList: List<String>
//
//    @SuppressLint("ClickableViewAccessibility")
//    private fun initializeGame() {
//        // Remove shuffle and use the pairs in a predictable order
//        letterList = wordMeaningPairs.map { it.first }
//        picList = wordMeaningPairs.map { it.second }
//
//        binding.text1.text = letterList[0]
//        binding.text2.text = letterList[1]
//        binding.text3.text = letterList[2]
//
//        binding.pic1.text = picList[0]
//        binding.pic2.text = picList[1]
//        binding.pic3.text = picList[2]
//
//        // Set touch listeners for matching
//        binding.text1.setOnTouchListener { v, event -> handleTouchEvent(v, event, 0) }
//        binding.text2.setOnTouchListener { v, event -> handleTouchEvent(v, event, 1) }
//        binding.text3.setOnTouchListener { v, event -> handleTouchEvent(v, event, 2) }
//
//        binding.pic1.setOnTouchListener { v, event -> handleTouchEvent(v, event, 0) }
//        binding.pic2.setOnTouchListener { v, event -> handleTouchEvent(v, event, 1) }
//        binding.pic3.setOnTouchListener { v, event -> handleTouchEvent(v, event, 2) }
//    }
//
//    private fun handleTouchEvent(v: View, event: MotionEvent, index: Int): Boolean {
//        // Get the position of the touched view
//        val (x, y) = getCenterCoordinates(v)
//
//        // Check if there's a match based on the selected letter and its corresponding meaning
//        if (event.action == MotionEvent.ACTION_UP) {
//            if (letterList[index] == picList[index]) {
//                // If matched, handle the correct action (e.g., show correct feedback)
//                v.setBackgroundColor(Color.GREEN) // Example feedback for correct match
//            } else {
//                // If not matched, apply shake animation to the definition
//                val definitionView = when (index) {
//                    0 -> binding.pic1
//                    1 -> binding.pic2
//                    2 -> binding.pic3
//                    else -> return false
//                }
//                shakeAnimation(definitionView, x, y)
//            }
//        }
//        return true
//    }
//
//    private fun shakeAnimation(view: View, x: Float, y: Float) {
//        val shake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
//        view.startAnimation(shake)
//    }
//
//
////    private fun initializeGame() {
////        letterList = chunked[currentListChunkPos]
////        if (letterList.size == 2) binding.imgNext.visibility = View.GONE
////        picList = letterList.shuffled()
////        for (i in letterList.indices) {
////            getLetterText(i, letterList[i])  // تم تعديلها لتعبئة النصوص بدلاً من الصور
////        }
////        for (i in picList.indices) {
////            getPicText(i, picList[i])  // تم تعديلها لتعبئة النصوص بدلاً من الصور
////        }
////    }
//
//    private fun moveToNextSet() {
//        if (chunked.size > currentListChunkPos + 1) {
//            currentListChunkPos++
//            initializeGame()
//        } else {
//            Toast.makeText(requireContext(), "Finish", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private fun textTouchListeners() {  // تم تعديلها لتكون خاصة بالنصوص
//        binding.text1.setOnTouchListener { _, event -> handleTouch(event, binding.text1) }
//        binding.text2.setOnTouchListener { _, event -> handleTouch(event, binding.text2) }
//        binding.text3.setOnTouchListener { _, event -> handleTouch(event, binding.text3) }
//    }
//
//    private fun handleTouch(event: MotionEvent, textView: TextView): Boolean {
//        val coordinates = getCenterCoordinates(textView)
//        val startX = coordinates.first
//        val startY = coordinates.second
//
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                lineView.startCoordinates = coordinates
//                lineView.endCoordinates = null
//                lineView.invalidate()
//            }
//            MotionEvent.ACTION_MOVE -> {
//                lineView.endCoordinates = Pair(event.x + startX, event.y + startY)
//                lineView.invalidate()
//            }
//            MotionEvent.ACTION_UP -> {
//                val endX = lineView.endCoordinates?.first!!
//                val endY = lineView.endCoordinates?.second!!
//                endLineDraw(textView, endX, endY)
//                lineView.invalidate()
//            }
//        }
//        return true
//    }
//
//    private fun endLineDraw(textView: TextView, x: Float, y: Float) {
//        val hitRect = Rect()
//        shakeAnimation(textView, x, y)
//        when (textView.contentDescription) {
//            binding.pic1.contentDescription -> validateConnection(textView, hitRect, x, y, binding.pic1)
//            binding.pic2.contentDescription -> validateConnection(textView, hitRect, x, y, binding.pic2)
//            else -> validateConnection(textView, hitRect, x, y, binding.pic3)
//        }
//    }
//
//    private fun validateConnection(
//        textView: TextView,
//        hitRect: Rect,
//        x: Float,
//        y: Float,
//        picture: TextView
//    ) {
//        picture.getHitRect(hitRect)
//        if (hitRect.contains(x.toInt(), y.toInt())) {
//            binding.imgNext.visibility = View.VISIBLE
//            lineView.visibility = View.INVISIBLE
//        }
//    }
//
//    private fun getCenterCoordinates(view: View): Pair<Float, Float> {
//        val x = (view.x + view.width / 2).toFloat()
//        val y = (view.y + view.height / 2).toFloat()
//        return Pair(x, y)
//    }
//
//    private fun getLetterText(index: Int, text: String) {
//        when (index) {
//            0 -> binding.text1.text = text
//            1 -> binding.text2.text = text
//            2 -> binding.text3.text = text
//        }
//    }
//
//    private fun getPicText(index: Int, pic: String) {
//        when (index) {
//            0 -> binding.pic1.text = pic
//            1 -> binding.pic2.text = pic
//            2 -> binding.pic3.text = pic
//        }
//    }
//
//}
//*****************الصور**********************
//
//package com.example.matchingactiviy
//
//import android.annotation.SuppressLint
//import android.graphics.Rect
//import android.os.Bundle
//import android.speech.tts.TextToSpeech
//import android.view.LayoutInflater
//import android.view.MotionEvent
//import android.view.View
//import android.view.ViewGroup
//import android.view.animation.AnimationUtils
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
////import com.bumptech.binding.binding
//import coil.load
//import com.example.matchingactiviy.databinding.FragmentMatchingBinding
//
//class MatchingFragment : Fragment() {
//
//    private lateinit var binding: FragmentMatchingBinding
//    private lateinit var lineView: LineView
//    private var tts: TextToSpeech? = null
//    private val chunkSize = 3
//    private val chunked = Constant.LettersList.chunked(chunkSize)
//    private var letterList = listOf<String>()
//    private var picList = listOf<String>()
//    private var currentListChunkPos = 0
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentMatchingBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        lineView = binding.lineview
//        initializeGame()
//
//        binding.imgClose.setOnClickListener {
//            requireActivity().supportFragmentManager.popBackStack()
//        }
//
//        binding.imgNext.setOnClickListener {
//            moveToNextSet()
//        }
//
//        imageTouchListeners()
//    }
//
//    private fun initializeGame() {
//        letterList = chunked[currentListChunkPos]
//        if (letterList.size == 2) binding.imgNext.visibility = View.GONE
//        picList = letterList.shuffled()
//        for (i in letterList.indices) {
//            getLetterImage(i, letterList[i])
//        }
//        for (i in picList.indices) {
//            getPicImage(i, picList[i])
//        }
//    }
//
//    private fun moveToNextSet() {
//        if (chunked.size > currentListChunkPos + 1) {
//            currentListChunkPos++
//            initializeGame()
//        } else {
//            Toast.makeText(requireContext(), "Finish", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private fun imageTouchListeners() {
//        binding.img1.setOnTouchListener { _, event -> handleTouch(event, binding.img1) }
//        binding.img2.setOnTouchListener { _, event -> handleTouch(event, binding.img2) }
//        binding.img3.setOnTouchListener { _, event -> handleTouch(event, binding.img3) }
//    }
//
//    private fun handleTouch(event: MotionEvent, imageView: ImageView): Boolean {
//        val coordinates = getCenterCoordinates(imageView)
//        val startX = coordinates.first
//        val startY = coordinates.second
//
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                lineView.startCoordinates = coordinates
//                lineView.endCoordinates = null
//                lineView.invalidate()
//            }
//            MotionEvent.ACTION_MOVE -> {
//                lineView.endCoordinates = Pair(event.x + startX, event.y + startY)
//                lineView.invalidate()
//            }
//            MotionEvent.ACTION_UP -> {
//                val endX = lineView.endCoordinates?.first!!
//                val endY = lineView.endCoordinates?.second!!
//                endLineDraw(imageView, endX, endY)
//                lineView.invalidate()
//            }
//        }
//        return true
//    }
//
//    private fun endLineDraw(imageView: ImageView, x: Float, y: Float) {
//        val hitRect = Rect()
//        shakeAnimation(imageView, x, y)
//        when (imageView.contentDescription) {
//            binding.pic1.contentDescription -> validateConnection(imageView, hitRect, x, y, binding.pic1)
//            binding.pic2.contentDescription -> validateConnection(imageView, hitRect, x, y, binding.pic2)
//            else -> validateConnection(imageView, hitRect, x, y, binding.pic3)
//        }
//    }
//
//    private fun validateConnection(
//        imageView: ImageView,
//        hitRect: Rect,
//        x: Float,
//        y: Float,
//        targetView: ImageView
//    ) {
//        targetView.getHitRect(hitRect)
//        val isTouched = hitRect.contains(x.toInt(), y.toInt())
//        if (isTouched) {
//            imageView.isEnabled = false
//            lineView.endCoordinates = getCenterCoordinates(targetView)
//            val line = LineView.LineList(lineView.startCoordinates, lineView.endCoordinates)
//            lineView.lineList.add(line)
//            if (lineView.lineList.size == 3) {
//                binding.imgNext.visibility = View.VISIBLE
//            }
//        } else {
//            lineView.endCoordinates = null
//        }
//    }
//
//    private fun shakeAnimation(image: ImageView, x: Float, y: Float) {
//        val hitRect1 = Rect()
//        val hitRect2 = Rect()
//        val hitRect3 = Rect()
//        binding.pic1.getHitRect(hitRect1)
//        binding.pic2.getHitRect(hitRect2)
//        binding.pic3.getHitRect(hitRect3)
//        if (hitRect1.contains(x.toInt(), y.toInt()) && image.contentDescription != binding.pic1.contentDescription) {
//            binding.pic1.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.shake))
//        }
//        if (hitRect2.contains(x.toInt(), y.toInt()) && image.contentDescription != binding.pic2.contentDescription) {
//            binding.pic2.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.shake))
//        }
//        if (hitRect3.contains(x.toInt(), y.toInt()) && image.contentDescription != binding.pic3.contentDescription) {
//            binding.pic3.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.shake))
//        }
//    }
//
//    private fun getCenterCoordinates(view: View): Pair<Float, Float> {
//        val x = view.x + view.width / 2
//        val y = view.y + view.height / 2
//        return Pair(x, y)
//    }
//
//    private fun getLetterImage(position: Int, letter: String) {
////        val path = AppUtils.getAlphabetConnectPath(letter)
////        val bm = AppUtils.getBitmapByAssetName(path, requireContext())
//
//        val resId = when(letter) {
//            "A" -> R.drawable.a
//            "B" -> R.drawable.b
//            "C" -> R.drawable.c
//            else -> R.drawable.a  // default case
//        }
//        when (position) {
//            0 -> {
//                binding.img1.contentDescription = letter
//                binding.img1.load(resId)
//            }
//            1 -> {
//                binding.img2.contentDescription = letter
//                binding.img2.load(resId)
//            }
//            else -> {
//                binding.img3.contentDescription = letter
//                binding.img3.load(resId)
//            }
//        }
//    }
//
//    private fun getPicImage(position: Int, letter: String) {
////        val path = AppUtils.getAlphabetObjectPath(letter)
////        val bm = AppUtils.getBitmapByAssetName(path, requireContext())
//
//        val resId = when(letter) {
//            "A" -> R.drawable.a_ballon
//            "B" -> R.drawable.b_ballon
//            "C" -> R.drawable.c_ballon
//            else -> R.drawable.a_ballon  // default case
//        }
//        when (position) {
//            0 -> {
//                binding.pic1.contentDescription = letter
//                binding.pic1.load(resId)
//            }
//            1 -> {
//                binding.pic2.contentDescription = letter
//                binding.pic2.load(resId)
//            }
//            else -> {
//                binding.pic3.contentDescription = letter
//                binding.pic3.load(resId)
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        tts?.shutdown()
//    }
//}
