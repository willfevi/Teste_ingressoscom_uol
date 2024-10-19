package com.example.ingressoscom.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Trailer(
    val type: String,
    val url: String,
    val embeddedUrl: String
) : Parcelable
