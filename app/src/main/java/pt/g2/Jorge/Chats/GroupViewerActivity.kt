package pt.g2.Jorge.Chats

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pt.g2.Jorge.R

class GroupViewerActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_viewer)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        val backspaceGroup= findViewById<ImageView>(R.id.backspaceGroupList)
        val groupList = findViewById<ListView>(R.id.groupList)

        val userNamesList = ArrayList<String>()
        val chatIdsList = ArrayList<String>() // Add list to hold chat IDs

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userNamesList)
        groupList.adapter = adapter

        val currentUserId = auth.currentUser?.uid



        firestore.collection("chats")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Retrieve the chat details from the current chat document
                    val chatName = document["name"] as? String
                    val chatId = document.id // Retrieve chat ID directly from document
                    val userIds = document["userIds"] as? List<String>

                    if (chatName != null && chatId != null && userIds != null && userIds.contains(currentUserId) && userIds.size >= 3) {
                        // If the current user is a member and the chat has 3 or more members
                        // Add the chat details to the lists
                        userNamesList.add(chatName)
                        chatIdsList.add(chatId) // Add chat ID to chatIdsList
                    }
                }
                // Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Log.e(TAG, "Error getting chats", exception)
            }


        groupList.setOnItemClickListener { _, _, position, _ ->
            val selectedChatName = userNamesList[position]
            val chatId = chatIdsList[position]
            Log.d("Groupos",selectedChatName)
            openChatActivity(selectedChatName, currentUserId!!,chatId )
        }

        backspaceGroup.setOnClickListener {
            finish()
        }
    }

    private fun openChatActivity(selectedUserName: String, userId: String, chatId: String?) {
        //Log.d("username", "Entrei")
        //Log.d("username", "ChatID: $chatId")

        val chatActivityIntent = Intent(this, ChatActivity::class.java)
        chatActivityIntent.putExtra("userName", selectedUserName)
        chatActivityIntent.putExtra("userId", userId)
        chatActivityIntent.putExtra("chatId", chatId)
        startActivity(chatActivityIntent)
    }

}
