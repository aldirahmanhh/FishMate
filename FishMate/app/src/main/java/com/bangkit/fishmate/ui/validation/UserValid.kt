package com.bangkit.fishmate.ui.validation

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.bangkit.fishmate.R

class UserValid @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        hint = context.getString(R.string.hint_username)
        isFocusableInTouchMode = true
        setupValidation()
    }

    private fun setupValidation() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validateUsername(s.toString())
            }
        })

        onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateUsername(text.toString())
            }
        }

        setOnClickListener {
            requestFocus()
        }
    }

    private fun validateUsername(input: String) {
        val errorMessage = when {
            input.isEmpty() -> context.getString(R.string.username_required)
            else -> null
        }

        error = errorMessage
        contentDescription = errorMessage ?: context.getString(R.string.username_required)
    }
}
