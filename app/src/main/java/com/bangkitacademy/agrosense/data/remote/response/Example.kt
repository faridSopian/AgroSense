package com.bangkitacademy.agrosense.data.remote.response

data class Example(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<DT>,
    val message: Int
)