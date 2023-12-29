package pt.g2.Jorge.Chats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.google.ai.client.generativeai.Chat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.auth.User
import pt.g2.Jorge.Adapters.MessageAdapter
import pt.g2.Jorge.R
import java.util.Calendar

class ChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        auth = Firebase.auth



        findViewById<FloatingActionButton>(R.id.SendMessage).setOnClickListener {
            sendMessage()
        }

    }

    private fun sendMessage() {

        var text = findViewById<EditText>(R.id.TextMessage).text.toString()


    }
}