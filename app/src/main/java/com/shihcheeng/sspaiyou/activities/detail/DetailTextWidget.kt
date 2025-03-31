package com.shihcheeng.sspaiyou.activities.detail

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import com.google.android.material.textview.MaterialTextView

class DetailTextWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleRes: Int = 0
) : MaterialTextView(context, attrs, defStyleRes){

    private var onSingleTap: OnClickListener? = null

    private val gestureDetector = GestureDetector(context, InnerGestureDec())

    inner class InnerGestureDec : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            onSingleTap?.onClick(this@DetailTextWidget)
            return true
        }
    }

    init {
        setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            performClick()
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP -> requestFocus()
        }
        performClick()
        return super.onTouchEvent(event)
    }

    fun setOnSingleClick(l: OnClickListener) {
        if (!isClickable) {
            isClickable = true
        }
        onSingleTap = l
    }

}