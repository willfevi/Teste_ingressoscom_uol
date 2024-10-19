package com.example.ingressoscom.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    val url: String,
    val type: String
): Parcelable