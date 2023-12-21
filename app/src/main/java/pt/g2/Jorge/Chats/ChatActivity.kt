package pt.g2.Jorge.Chats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.auth.User
import pt.g2.Jorge.R

class ChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth



    private lateinit var currentChannelId: String
    private lateinit var currentUser: User
    private lateinit var otherUserId: String

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

    }


}