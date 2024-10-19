package com.example.ingressoscom.presentation.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: String,
    val title: String,
    val type: String,
    val synopsis: String,
    val cast: String,
    val director: String,
    val inPreSale: Boolean,
    val isReexhibition: Boolean,
    val isPlaying: Boolean,
    val premiereDate: PremiereDate?,
    val images: List<Image>?,
    val trailers: List<Trailer>?,
    val genres: List<String>?,
    @SerializedName("contentRating") val contentRating: String?,
    @SerializedName("duration") val duration: String?,
    @SerializedName("rating") val rating: Double?
) : Parcelable
