package pt.g2.Jorge.Chats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import pt.g2.Jorge.Adapters.ChatMessage
import pt.g2.Jorge.Adapters.MessageAdapter

import pt.g2.Jorge.R
import java.util.Date
import kotlin.math.roundToInt


class ChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var messagesCollection: CollectionReference
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messagesAdapter: MessageAdapter
    private lateinit var messagesListener: ListenerRegistration
    private val messagesList = ArrayList<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val userName =
            intent.getStringExtra("userName") // retrieve the username from the previous activity
        val userMessageId = intent.getStringExtra("userId")
        val messageId = intent.getStringExtra("chatId")

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        Log.d("username", "Chat Activity UserName = $userName") // user que manda msg
        Log.d("username", "Chat Activity User Message ID = $userMessageId") // id do user que manda
        Log.d("username", "Chat Activity Message ID = $messageId") // id mensagem


        val sendButton = findViewById<ImageView>(R.id.sendMessage)
        val backspace = findViewById<ImageView>(R.id.backspace)


        messagesRecyclerView = findViewById(R.id.message)
        messagesAdapter = MessageAdapter(messagesList, auth.currentUser?.uid.orEmpty())
        messagesRecyclerView.adapter = messagesAdapter
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)

        if (messageId != null) {
            setupMessagesListener(messageId)
        }

        sendButton.setOnClickListener {
            //Log.d("username", "ehehe")
            if (userMessageId != null && messageId != null) {
                sendMessage(userMessageId, messageId)
            }
        }

        if (userName != null) {
            //Log.d("username", "Entrei")
            writeProfileName(userName)
        }

        backspace.setOnClickListener {
            finish() // go back to the  chat list activity
        }


    }

    /**
     * func to show chats done when a chat is opened
     */
    private fun setupMessagesListener(messageId: String) {

        val messagesCollection =
            firestore.collection("chats").document(messageId).collection("messages")

        // Get the current message's timestamp
        val currentMessageTimestamp = Date().time

        messagesListener = messagesCollection.orderBy("date").endAt(currentMessageTimestamp)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && !snapshot.isEmpty) {
                    messagesList.clear()

                    for (document in snapshot.documents) {
                        val chatMessage = document.toObject(ChatMessage::class.java)
                        if (chatMessage != null) {
                            messagesList.add(chatMessage)
                        }
                    }

                    // Sort messages after adding them to the list
                    messagesList.sortBy { it.date }

                    messagesAdapter.notifyDataSetChanged()
                    messagesRecyclerView.scrollToPosition(messagesList.size - 1)
                }
            }

    }

    /**
     * func that stores the messages in firebase
     */

    private fun sendMessage(userMessageId: String, messageId: String) {
        val messageBox = findViewById<EditText>(R.id.messageBox)
        val messageText = messageBox.text.toString()

        if (messageText.isNotEmpty()) {
            val currentUserId = auth.currentUser?.uid.orEmpty()

            if (currentUserId.isNotEmpty()) {
                val message = ChatMessage(userMessageId, messageId, currentUserId, Date().time, messageText, 1, emptyList())

                val messagesCollection = firestore.collection("chats").document(messageId).collection("messages")

                // Add the message to Firestore
                messagesCollection.add(message)
                    .addOnSuccessListener { documentReference ->
                        // Message has been successfully added to Firestore, update UI
                        val messageId = documentReference.id
                        message.id = messageId

                        // Add the new message to the messagesList
                        messagesList.add(message)

                        // Notify the adapter that a new item has been inserted
                        messagesAdapter.notifyItemInserted(messagesList.size - 1)

                        messageBox.text.clear() // clear write box
                        messagesRecyclerView.scrollToPosition(messagesList.size - 1)

                        Log.d("username", "Message sent successfully")
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                        Log.d("username", "Error sending message: $e")
                        Toast.makeText(applicationContext, "Error sending message", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(applicationContext, "Message is empty", Toast.LENGTH_SHORT).show()
        }
    }



    /**
     * fun to change the username of the people to send the message
     */
    private fun writeProfileName(userName:String){
        //Log.d("username","Aqui")
        val profileName = findViewById<TextView>(R.id.profileName)
        profileName.text=userName
    }
}





