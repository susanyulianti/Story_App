package com.susan.mystories

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat

class StoriesEditText : AppCompatEditText, View.OnTouchListener{

    private lateinit var iconWarning: Drawable
    private lateinit var iconPassword: Drawable

    private fun init(){
        iconWarning = ContextCompat.getDrawable(context, R.drawable.ic_warning) as Drawable
        iconPassword = ContextCompat.getDrawable(context, R.drawable.ic_password) as Drawable

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length < 6){
                    showWarning()
                }else{
                    hideWarning()
                }
            }
            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun showWarning() {
        setWarningDrawables(startOfTheText = iconPassword, endOfTheText = iconWarning)
    }
    private fun hideWarning() {
        setWarningDrawables(startOfTheText = iconPassword, endOfTheText = null)
    }

    private fun setWarningDrawables(startOfTheText: Drawable? = null, topOfTheText: Drawable? = null, endOfTheText: Drawable? = null, bottomOfTheText: Drawable? = null){
        // Sets the Drawables to appear to the left of,
        // above, to the right of, and below the text.
        setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (iconWarning.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearButtonEnd -> isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - iconWarning.intrinsicWidth).toFloat()
                when {
                    event.x > clearButtonStart -> isClearButtonClicked = true
                }
            }
            if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        iconWarning = ContextCompat.getDrawable(context, R.drawable.ic_warning) as Drawable
                        showWarning()
                        if (v != null) {
                            Toast.makeText(v.context, "asdas", Toast.LENGTH_SHORT).show()
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {

                        iconWarning = ContextCompat.getDrawable(context, R.drawable.ic_warning) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideWarning()
                        return true
                    }
                    else -> return false
                }
            }
        }
        return false
    }
}