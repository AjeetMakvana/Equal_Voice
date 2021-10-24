package com.xlentdevs.spectrum.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar


fun View.forceHideKeyboard() {
    val inputManager: InputMethodManager =
        this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(this.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun View.showSnackBar(text: String, viewId: Int) {
    Snackbar.make(this.rootView.findViewById(viewId), text, Snackbar.LENGTH_SHORT).show()
}