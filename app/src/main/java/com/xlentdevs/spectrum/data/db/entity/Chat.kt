package com.xlentdevs.spectrum.data.db.entity

data class Chat(
    var lastMessage: Message = Message(),
    var info: ChatInfo = ChatInfo()
)

data class ChatInfo(
    var id: String = ""
)