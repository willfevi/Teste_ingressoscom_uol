package com.example.ingressoscom.presentation.view.adapter

import java.text.SimpleDateFormat
import java.util.Locale

fun formatReleaseDate(dateString: String?): String {
    if (dateString.isNullOrEmpty()) {
        return "Data não disponível"
    }

    return try {
        val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = isoFormat.parse(dateString)

        val brFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        brFormat.format(date)
    } catch (e: Exception) {
        "Data inválida"
    }
}
