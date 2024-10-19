package com.example.ingressoscom.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PremiereDate(
    val localDate: String,
    val isToday: Boolean,
    val dayOfWeek: String,
    val dayAndMonth: String,
    val hour: String,
    val year: String
) : Parcelable