package pt.g2.Jorge.Chats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.ai.client.generativeai.Chat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.auth.User
import pt.g2.Jorge.R

class ChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    var firebaseUser: FirebaseUser? =null
    var reference : DatabaseReference?=null
    var chatList = ArrayList<Chat>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        auth = Firebase.auth

        var send_Button = findViewById<Button>(R.id.SendMessage)

        send_Button.setOnClickListener {
            sendMessage()
        }

    }

    private fun sendMessage(){

        var text = findViewById<EditText>(R.id.TextMessage).text.toString()

        var sender = findViewById<TextView>(R.id.textView_message_text)

        sender.text = text

    }


}