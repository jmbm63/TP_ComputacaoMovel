package pt.g2.Jorge.Chats

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import pt.g2.Jorge.Adapters.chat
import pt.g2.Jorge.R


class ChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val userName = intent.getStringExtra("userName")
        Log.d("username", "$userName")

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val sendButton = findViewById<ImageView>(R.id.sendMessage)

        sendButton.setOnClickListener {
            Log.d("username", "ehehe")
        }

        if (userName != null) {
            Log.d("username", "Entrei")
            writeProfileName(userName)
        }
    }


    /**
     * fun to change the username of the people to send the message
     */
    private fun writeProfileName(userName:String){
        Log.d("username","Aqui")
        val profileName = findViewById<TextView>(R.id.profileName)
        profileName.text=userName
    }

}




