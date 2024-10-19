package com.example.ingressoscom.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieResponse(
    val items: List<Movie>
): Parcelable
