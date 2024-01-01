package pt.g2.Jorge.Chats

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.firestore.auth.User
import pt.g2.Jorge.Adapters.UserAdapter
import pt.g2.Jorge.Login.MainActivity
import pt.g2.Jorge.R

class ChatList : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)
        auth = FirebaseAuth.getInstance()

        // Use Firebase.auth instead of Firebase.auth if you are using Firebase Auth
        // auth = Firebase.auth

        database = FirebaseDatabase.getInstance("https://g2teste-a0ef3-default-rtdb.europe-west1.firebasedatabase.app/").getReference("user")

        val listView = findViewById<ListView>(R.id.chatList)
        val userNamesList = ArrayList<String>()

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                dataChange(listView, dataSnapshot, userNamesList)// pass listview, userNamesList and dataSnapshot

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                show()
            }
        })

        // when is pressed a chat
        listView.setOnItemClickListener { parent, view, position, id ->
            updateActivity()
        }

    }

    private fun show() {
        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
    }


    /**u@gmail.com
     * When pressed a chat, it opens a new activity
     */
    private fun updateActivity() {
        val intent = Intent(this, ChatActivity::class.java)
        startActivity(intent)
    }

    private fun dataChange(listView: ListView, dataSnapshot: DataSnapshot, userNamesList: ArrayList<String>) {

        for (childSnapshot in dataSnapshot.children) {
            if (childSnapshot.value is String) {

                val userName = childSnapshot.value as String
                userNamesList.add(userName) // add the user name to the list
            }
        }

        // Update the ListView adapter
        val adapter = ArrayAdapter(this@ChatList, android.R.layout.simple_list_item_1, userNamesList)
        listView.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}



