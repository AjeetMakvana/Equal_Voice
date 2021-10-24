package com.xlentdevs.spectrum.data.db.entity

import java.util.*

data class Message(
    var senderId: String = "",
    var text: String = "",
    var epochTimeMs: Long = Date().time,
    var seen: Boolean = false
)