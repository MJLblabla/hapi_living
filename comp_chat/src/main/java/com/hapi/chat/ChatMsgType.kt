package com.hapi.chat

enum class ChatMsgType(val type:String) {
    TEXT("text"),
    FILE("file"),
    PIC("PicMessage"),
    UNKOWN("UnkownMessage"),
    VOICE("Voice")
}