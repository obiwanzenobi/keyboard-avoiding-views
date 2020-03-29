package com.wojciechwarwas.keyboard_avoiding_views

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import android.util.AttributeSet
import androidx.constraintlayout.widget.Guideline

private const val THRESHOLD = 10

class KeyboardAvoidingGuideline @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Guideline(context, attrs, defStyleAttr) {

    var changeListener: (() -> Unit)? = null

    var initialFrame = 0
    var currentFrame = 0

    init {
        val r = Rect()
        getActivity(context)?.window?.decorView?.getWindowVisibleDisplayFrame(r)
        initialFrame = (r.bottom - r.top)
        currentFrame = initialFrame

        getActivity(context)?.window?.decorView?.viewTreeObserver?.addOnPreDrawListener {
            getActivity(context)?.window?.decorView?.getWindowVisibleDisplayFrame(r)
            val currentHeight = (r.bottom - r.top)
            val difference = (initialFrame - currentHeight)

            if (currentHeight == currentFrame) {
                return@addOnPreDrawListener true
            }

            currentFrame = currentHeight
            changeListener?.invoke()

            if (difference > THRESHOLD) {
                setGuidelineEnd(difference)
            } else {
                setGuidelineEnd(0)
            }

            return@addOnPreDrawListener true
        }
    }

    private fun getActivity(context: Context?): Activity? {
        if (context == null) return null
        if (context is Activity) return context
        return if (context is ContextWrapper) getActivity(context.baseContext) else null
    }
}
