package com.bangkit.fishmate.ui.validation

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import com.bangkit.fishmate.R

class NewPassValid @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        hint = context.getString(R.string.hint_new_password)
        isFocusableInTouchMode = true
        setupInputType()
        setupValidation()
    }

    private fun setupInputType() {
        inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
    }

    private fun setupValidation() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validatePassword(s.toString())
            }
        })
    }

    private fun validatePassword(input: String) {
        val errorMessage = when {
            input.isEmpty() -> context.getString(R.string.password_required)
            input.length < 8 -> context.getString(R.string.password_length_error)
            else -> null
        }

        error = errorMessage
    }
}