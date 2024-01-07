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
import com.google.firebase.firestore.auth.User
import pt.g2.Jorge.Adapters.UserAdapter
import pt.g2.Jorge.Adapters.chat
import pt.g2.Jorge.Login.MainActivity
import pt.g2.Jorge.R



class ChatList : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)
        auth = FirebaseAuth.getInstance()

        // Correct path to Firebase Realtime Database
        database = FirebaseDatabase.getInstance("https://g2teste-a0ef3-default-rtdb.europe-west1.firebasedatabase.app/").getReference("user")

        val listView = findViewById<ListView>(R.id.chatList)
        val userNamesList = ArrayList<String>()

        // Use a custom adapter if user information is more complex
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userNamesList)
        listView.adapter = adapter
        val currentId = auth.currentUser?.uid
        // ValueEventListener to listen for changes in the database
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                userNamesList.clear() // Clear the list before updating
                for (childSnapshot in dataSnapshot.children) {

                    val userId = childSnapshot.child("userId").getValue(String::class.java)
                    //Log.d("aqui","1->$userId")
                    //Log.d("aqui","2->$currentId")


                    if (userId != currentId) { // ensure that the current user is shown on the list
                        val userName = childSnapshot.child("userName").getValue(String::class.java)
                        userName?.let {
                            userNamesList.add(it)
                        }
                    }
                }
                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                //Log.d("erro","gg")
            }
        }

        // Add the ValueEventListener to the database reference
        database.addValueEventListener(valueEventListener)

        // when a chat is pressed
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedUserName = userNamesList[position]
            openChatActivity(selectedUserName)
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Open a new chat activity with the selected user's information
     */
    private fun openChatActivity(selectedUserName: String) {
        //Log.d("username","Entrei activity")

        val returnIntent = Intent(this@ChatList, ChatActivity::class.java)
        returnIntent.putExtra("userName", selectedUserName) // pass the username of the selected chat

        startActivity(returnIntent)
    }
}




