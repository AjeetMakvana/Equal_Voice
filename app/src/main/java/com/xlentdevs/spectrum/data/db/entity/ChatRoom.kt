package com.xlentdevs.spectrum.data.db.entity

data class ChatRoom(
    val uid: String = "",
    val roomName: String = "",
    val roomPhoto: String = "",
    val description: String = "",
    val tag1: String = "",
    val tag2: String = "",
    val tag3: String = "",
    val totalTags: Int = 0
)