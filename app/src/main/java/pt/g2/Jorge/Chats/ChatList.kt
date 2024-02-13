package pt.g2.Jorge.Chats

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pt.g2.Jorge.Adapters.chat
import pt.g2.Jorge.Profile.ProfileActivity
import pt.g2.Jorge.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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

                for (document in documents) { // pass through the database and retrieve all of the usernames so it can be show on the list
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
                Log.d("Firestore", "Error getting documents: ", exception)
            }

        // meter o id do men da lista pro chat
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedUserName = userNamesList[position]

            firestore.collection("users")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val userName = document.getString("userName")
                        if (userName == selectedUserName) {
                            val userId = document.getString("userId")
                            if (userId != null) {
                                storeChat(selectedUserName, userId)
                                break
                            }
                        }
                    }
                }
        }
    }

    /**
     * store chat on firebase
     */
    private fun storeChat(selectedUserName: String, userId: String) {
        findExistingChat(userId) { chatExists, chatId ->
            if (chatExists) {
                // Chat already exists, open the existing chat activity
                openChatActivity(selectedUserName, userId, chatId)
            } else {
                // If a chat document doesn't exist, create a new chat document
                val newChat = chat(
                    id = generateChatId(),
                    userIds = listOf(auth.currentUser?.uid.orEmpty(), userId).sorted(),
                    date = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date().time),
                    name = "Chat with $selectedUserName"
                )

                // Add the new chat document to the "chats" collection
                firestore.collection("chats").document(newChat.id).set(newChat)
                    .addOnSuccessListener {
                        Log.d("username", "Chat document created successfully")
                        openChatActivity(selectedUserName, userId, newChat.id)
                    }
                    .addOnFailureListener { e ->
                        Log.d("username", "Error creating chat document", e)
                    }
            }
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

    /**
     * seacrh to see if there is already one chat created between two users
     *
     * Esta função ta com um erro qlqr que nao tou a conseguir descobrir mas nao afeta o programa
     */

    private fun findExistingChat(userId: String, onComplete: (Boolean, String?) -> Unit) {
        val currentUserUid = auth.currentUser?.uid.orEmpty()

        firestore.collection("chats")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result

                    for (document in result) {
                        val userIds = document["userIds"] as? List<String>
                        if (userIds != null && currentUserUid in userIds && userId in userIds) {
                            // If a chat document exists, get the chat ID from the document
                            val chatId = document.id
                            Log.d("username", "Chat ID: $chatId")

                            onComplete(true, chatId)
                            return@addOnCompleteListener
                        }
                    }

                    onComplete(false, null)
                } else {
                    Log.e("username", "Error finding chat", task.exception)
                    onComplete(false, null)
                }
            }
        //Log.d("username", "userID: $userId")
        //Log.d("username",  auth.currentUser?.uid.orEmpty())
    }

    private fun generateChatId(length: Int = 20): String {
        val alphaNumeric = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return alphaNumeric.shuffled().take(length).joinToString("")
    }

    fun perfil(view: View) {
        val ProfileActivityIntent = Intent(this, ProfileActivity::class.java)
        startActivity(ProfileActivityIntent)
    }
}




