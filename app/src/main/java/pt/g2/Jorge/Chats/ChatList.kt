package pt.g2.Jorge.Chats


import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import pt.g2.Jorge.R

class ChatList : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)
        auth = Firebase.auth

        val listView = findViewById<ListView>(R.id.chatList)
        val values = listOf<String>("Jorge") // put diff chats on here
        var adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,values)
        listView.adapter = adapter


        listView.setOnItemClickListener { parent, view, position, id ->
            val chatIntent= Intent(this@ChatList, ChatActivity::class.java)
            startActivity(chatIntent)
        }

    }


}
