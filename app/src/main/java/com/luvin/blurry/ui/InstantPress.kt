package com.luvin.blurry.ui

import android.animation.ValueAnimator
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View

class InstantPress : View.OnTouchListener {
    private var ofView: View? = null

    private val DURATION: Long = 100L
    private val ALPHA_PRESSED: Float = 0.6f
    private val SCALE_PRESSED: Float = 0.99f

    private lateinit var alphaAnimator: ValueAnimator
    private lateinit var scaleAnimator: ValueAnimator

    private fun cancelPress() {
        if (!wasPressed) return
        wasPressed = false

        alphaAnimator.apply {
            cancel()
            setFloatValues(ofView!!.alpha, ALPHA_PRESSED, 1f)
            start()
        }

        scaleAnimator.apply {
            cancel()
            setFloatValues(ofView!!.scaleX, SCALE_PRESSED, 1f)
            start()
        }
    }

    private var wasPressed: Boolean = false

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (ofView == null) {
            ofView = v
            createAnimators()
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                wasPressed = true

                alphaAnimator.apply {
                    cancel()
                    setFloatValues(ofView!!.alpha, ALPHA_PRESSED)
                    start()
                }

                scaleAnimator.apply {
                    cancel()
                    setFloatValues(1F, SCALE_PRESSED)
                    start()
                }

                return true
            }
            MotionEvent.ACTION_UP -> {
                cancelPress()

                if (isUpInside(event)) {
                    v.performClick()
                }

                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                cancelPress()

                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isUpInside(event)) {
                    cancelPress()
                }

                return true
            }
        }

        return false
    }

    private fun createAnimators() {
        alphaAnimator = ValueAnimator().apply {
            duration = DURATION

            addUpdateListener {
                ofView!!.alpha = it.animatedValue as Float
            }
        }

        scaleAnimator = ValueAnimator().apply {
            duration = DURATION

            addUpdateListener {
                val scale = it.animatedValue as Float
                ofView!!.scaleX = scale
                ofView!!.scaleY = scale
            }
        }
    }

    private fun isUpInside(e: MotionEvent): Boolean {
        val rect = Rect(
            ofView!!.left,
            ofView!!.top,
            ofView!!.right,
            ofView!!.bottom
        )

        return rect.contains(
            ofView!!.left + e.x.toInt(),
            ofView!!.top + e.y.toInt()
        )
    }
}