package com.irfannurrizki.panstoryapp.appuserinterface.appcustomview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.irfannurrizki.panstoryapp.R
import com.irfannurrizki.panstoryapp.appuserinterface.appactivity.AppLoginActivity.Companion.emailRegex

class CustomEmailEditText : AppCompatEditText{

        constructor(context: Context) : super(context) {
            init()
        }

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
            init()
        }

        constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
        ) {
            init()
        }

        private fun init() {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    // Do nothing.
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    error = if (s.isNotEmpty()) {
                        fun String.isEmailCorrect(): Boolean {
                            return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
                        }
                        if (!s.toString().isEmailCorrect()) {
                            context.getString(R.string.validation_email)
                        } else null
                    } else null
                }

                override fun afterTextChanged(s: Editable) {
                    // Do nothing.
                }
            })
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            hint = context.getString(R.string.hint_email)

            isSingleLine = true
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        }
    }



