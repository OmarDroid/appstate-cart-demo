package com.example.appstatecart.util

import java.util.Locale

object PriceFormatter {
    fun format(cents: Long): String =
        String.format(Locale.US, "$%d.%02d", cents / 100, cents % 100)
}
