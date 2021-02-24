package com.grand.duke.elliot.madras.check.moviesearchapp.util

import android.text.Html
import android.widget.TextView

fun TextView.setHtmlText(htmlText: String) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
        text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
    else
        @Suppress("DEPRECATION")
        text = Html.fromHtml(htmlText)
}