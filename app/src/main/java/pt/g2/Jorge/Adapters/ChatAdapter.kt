package pt.g2.Jorge.Adapters

import java.io.Serializable


// esta class é para na lista do chats aparecer as mensagens etc, dps faz se
data class chat(
    var id: String,
    var userIds: List<String>,
    var date: String,
    var name: String
): Serializable
