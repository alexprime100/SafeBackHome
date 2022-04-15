package com.example.safebackhome

import android.text.InputType
import android.view.View
import android.widget.EditText

class test {
    fun delete(view: View) {
        val editText = EditText(view.context)
        editText.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    }
}