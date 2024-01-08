package pt.g2.Jorge.Chats

import android.app.Activity
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.auth.User
import pt.g2.Jorge.Adapters.UserAdapter
import pt.g2.Jorge.Adapters.chat
import pt.g2.Jorge.Login.MainActivity
import pt.g2.Jorge.R



class ChatList : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val listView = findViewById<ListView>(R.id.chatList)
        val userNamesList = ArrayList<String>()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userNamesList)
        listView.adapter = adapter

        val currentUserId = auth.currentUser?.uid

        firestore.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                userNamesList.clear()

                for (document in documents) {
                    val userId = document.getString("userId")

                    if (userId != currentUserId) {
                        val userName = document.getString("userName")
                        userName?.let {
                            userNamesList.add(it)
                        }
                    }
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedUserName = userNamesList[position]
            openChatActivity(selectedUserName)
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun openChatActivity(selectedUserName: String) {
        Log.d("username", "Entrei activity")
        Log.d("username", selectedUserName)

        val chatActivityIntent = Intent(this, ChatActivity::class.java)
        chatActivityIntent.putExtra("userName", selectedUserName)
        startActivity(chatActivityIntent)
    }
}




