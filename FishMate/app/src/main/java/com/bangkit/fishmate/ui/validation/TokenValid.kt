package com.bangkit.fishmate.ui.validation

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import com.bangkit.fishmate.R

class TokenValid @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        hint = context.getString(R.string.hint_token) // Token hint from strings.xml
        isFocusableInTouchMode = true
        setupInputType()
        setupValidation()
    }

    private fun setupInputType() {
        inputType = EditorInfo.TYPE_CLASS_TEXT // Atur input type menjadi text untuk token
    }

    private fun setupValidation() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validateToken(s.toString())
            }
        })
    }

    private fun validateToken(input: String) {
        val errorMessage = when {
            input.isEmpty() -> context.getString(R.string.token_required)
            input.length != 6 -> context.getString(R.string.token_length_error)
            !input.matches("^[a-zA-Z0-9]+\$".toRegex()) -> context.getString(R.string.token_invalid)
            else -> null
        }

        error = errorMessage
    }
}
