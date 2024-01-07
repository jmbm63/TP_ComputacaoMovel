package pt.g2.Jorge.Chats

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import pt.g2.Jorge.Adapters.chat

import pt.g2.Jorge.R

/**
 * This activity is used to hold the Messaging fragment
 */
class ChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var db: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val userName = intent.getStringExtra("userName")// retireve the username passed from chatList act
        //Log.d("username","$userName")

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("messages") // node of the database

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_messages)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val sendButton= findViewById<Button>(R.id.Send_Button)
        val textMessage = findViewById<EditText>(R.id.editText_message).text.toString() // retrieve message writen

        sendButton.setOnClickListener{
            if(textMessage.isEmpty()){
                Toast.makeText(applicationContext, "pls write message", Toast.LENGTH_LONG).show()
            }else{

                sendMessage()

               /* a funcao vai ter que passar este parametros
                var id:String,
                var chatId: String,
                var userId: String,
                var date: Double,  // timestamp
                var body: String,
                var messageType: Int,
                var readby: List<String>*/
            }
        }

    }
    private fun sendMessage(){

    }
}
