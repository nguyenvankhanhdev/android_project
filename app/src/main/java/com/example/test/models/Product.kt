package com.example.test.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    var product_id: String = "",
    val title: String = "",
    val price: String = "",
    val description: String = "",
    val image: String = "",
    val shoeTypeId:String= "",
) : Parcelable
