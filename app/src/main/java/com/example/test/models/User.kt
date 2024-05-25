package com.example.test.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
@Parcelize
data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val gender: String = "",
    val role: String = UserRole.USER.name,
    val profileCompleted: Int = 0)
    :Parcelable