package com.example.tiendapcgamer.data.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

fun formatearPesos(valor: Double): String {
    val symbols = DecimalFormatSymbols(Locale("es", "CO")).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }
    val df = DecimalFormat("#,###", symbols)
    return "$${df.format(valor)}"
}
