package com.example.test.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SizeProduct(
    var size_id: String = "",
    var size: Int = 0,
    var quantity: Int =0,
    var product_id: String = ""
) : Parcelable