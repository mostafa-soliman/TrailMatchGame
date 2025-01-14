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
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
//import com.bumptech.binding.binding
import coil.load
import com.example.matchingactiviy.databinding.FragmentMatchingBinding

class MatchingFragment : Fragment() {

    private lateinit var binding: FragmentMatchingBinding
    private lateinit var lineView: LineView
    private var tts: TextToSpeech? = null
    private val chunkSize = 3
    private val chunked = Constant.LettersList.chunked(chunkSize)
    private var letterList = listOf<String>()
    private var picList = listOf<String>()
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

        imageTouchListeners()
    }

    private fun initializeGame() {
        letterList = chunked[currentListChunkPos]
        if (letterList.size == 2) binding.imgNext.visibility = View.GONE
        picList = letterList.shuffled()
        for (i in letterList.indices) {
            getLetterImage(i, letterList[i])
        }
        for (i in picList.indices) {
            getPicImage(i, picList[i])
        }
    }

    private fun moveToNextSet() {
        if (chunked.size > currentListChunkPos + 1) {
            currentListChunkPos++
            initializeGame()
        } else {
            Toast.makeText(requireContext(), "Finish", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun imageTouchListeners() {
        binding.img1.setOnTouchListener { _, event -> handleTouch(event, binding.img1) }
        binding.img2.setOnTouchListener { _, event -> handleTouch(event, binding.img2) }
        binding.img3.setOnTouchListener { _, event -> handleTouch(event, binding.img3) }
    }

    private fun handleTouch(event: MotionEvent, imageView: ImageView): Boolean {
        val coordinates = getCenterCoordinates(imageView)
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
                endLineDraw(imageView, endX, endY)
                lineView.invalidate()
            }
        }
        return true
    }

    private fun endLineDraw(imageView: ImageView, x: Float, y: Float) {
        val hitRect = Rect()
        shakeAnimation(imageView, x, y)
        when (imageView.contentDescription) {
            binding.pic1.contentDescription -> validateConnection(imageView, hitRect, x, y, binding.pic1)
            binding.pic2.contentDescription -> validateConnection(imageView, hitRect, x, y, binding.pic2)
            else -> validateConnection(imageView, hitRect, x, y, binding.pic3)
        }
    }

    private fun validateConnection(
        imageView: ImageView,
        hitRect: Rect,
        x: Float,
        y: Float,
        targetView: ImageView
    ) {
        targetView.getHitRect(hitRect)
        val isTouched = hitRect.contains(x.toInt(), y.toInt())
        if (isTouched) {
            imageView.isEnabled = false
            lineView.endCoordinates = getCenterCoordinates(targetView)
            val line = LineView.LineList(lineView.startCoordinates, lineView.endCoordinates)
            lineView.lineList.add(line)
            if (lineView.lineList.size == 3) {
                binding.imgNext.visibility = View.VISIBLE
            }
        } else {
            lineView.endCoordinates = null
        }
    }

    private fun shakeAnimation(image: ImageView, x: Float, y: Float) {
        val hitRect1 = Rect()
        val hitRect2 = Rect()
        val hitRect3 = Rect()
        binding.pic1.getHitRect(hitRect1)
        binding.pic2.getHitRect(hitRect2)
        binding.pic3.getHitRect(hitRect3)
        if (hitRect1.contains(x.toInt(), y.toInt()) && image.contentDescription != binding.pic1.contentDescription) {
            binding.pic1.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.shake))
        }
        if (hitRect2.contains(x.toInt(), y.toInt()) && image.contentDescription != binding.pic2.contentDescription) {
            binding.pic2.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.shake))
        }
        if (hitRect3.contains(x.toInt(), y.toInt()) && image.contentDescription != binding.pic3.contentDescription) {
            binding.pic3.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.shake))
        }
    }

    private fun getCenterCoordinates(view: View): Pair<Float, Float> {
        val x = view.x + view.width / 2
        val y = view.y + view.height / 2
        return Pair(x, y)
    }

    private fun getLetterImage(position: Int, letter: String) {
//        val path = AppUtils.getAlphabetConnectPath(letter)
//        val bm = AppUtils.getBitmapByAssetName(path, requireContext())

        val resId = when(letter) {
            "A" -> R.drawable.a
            "B" -> R.drawable.b
            "C" -> R.drawable.c
            else -> R.drawable.a  // default case
        }
        when (position) {
            0 -> {
                binding.img1.contentDescription = letter
                binding.img1.load(resId)
            }
            1 -> {
                binding.img2.contentDescription = letter
                binding.img2.load(resId)
            }
            else -> {
                binding.img3.contentDescription = letter
                binding.img3.load(resId)
            }
        }
    }

    private fun getPicImage(position: Int, letter: String) {
//        val path = AppUtils.getAlphabetObjectPath(letter)
//        val bm = AppUtils.getBitmapByAssetName(path, requireContext())

        val resId = when(letter) {
            "A" -> R.drawable.a_ballon
            "B" -> R.drawable.b_ballon
            "C" -> R.drawable.c_ballon
            else -> R.drawable.a_ballon  // default case
        }
        when (position) {
            0 -> {
                binding.pic1.contentDescription = letter
                binding.pic1.load(resId)
            }
            1 -> {
                binding.pic2.contentDescription = letter
                binding.pic2.load(resId)
            }
            else -> {
                binding.pic3.contentDescription = letter
                binding.pic3.load(resId)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.shutdown()
    }
}
