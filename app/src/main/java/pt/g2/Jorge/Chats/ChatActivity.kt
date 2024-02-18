package pt.g2.Jorge.Chats

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.firebase.storage.FirebaseStorage
import pt.g2.Jorge.Adapters.ChatMessage
import pt.g2.Jorge.Adapters.MessageAdapter
import pt.g2.Jorge.Adapters.MessageType

import pt.g2.Jorge.R
import java.io.InputStream
import java.util.Date


class ChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var messagesCollection: CollectionReference
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messagesAdapter: MessageAdapter
    private lateinit var messagesListener: ListenerRegistration
    private val messagesList = ArrayList<ChatMessage>()
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        val userName = intent.getStringExtra("userName") // retrieve the username from the previous activity
        val userMessageId = intent.getStringExtra("userId")
        val messageId = intent.getStringExtra("chatId")
        Log.d("username", "Chat Activity UserName = $userName") // user que manda msg
        Log.d("username", "Chat Activity User Message ID = $userMessageId") // id do user que manda
        Log.d("username", "Chat Activity Message ID = $messageId") // id mensagem


        val sendButton = findViewById<ImageView>(R.id.sendMessage)
        val backspace = findViewById<ImageView>(R.id.backspace)
        val senImageButton= findViewById<ImageView>(R.id.sendImage)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        messagesRecyclerView = findViewById(R.id.message)
        messagesAdapter = MessageAdapter(messagesList, auth.currentUser?.uid.orEmpty())
        messagesRecyclerView.adapter = messagesAdapter
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)

        if (messageId != null) {
            setupMessagesListener(messageId)
        }

        // send functions text and image
        sendButton.setOnClickListener {
            //Log.d("username", "ehehe")
            if (userMessageId != null && messageId != null) {
                sendMessage(userMessageId, messageId)
            }
        }

        senImageButton.setOnClickListener {
            if (userMessageId != null && messageId != null) {
                openImageFolder(userMessageId, messageId)
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

   override fun onStart() {
        super.onStart()
        val messageId = intent.getStringExtra("chatId")
        if (messageId != null) {
            setRealTimeMessage(messageId)
        }
   }

    // talvez possa ser retirado
    override fun onStop() {
        super.onStop()
        removeMessagesListener()
    }



    private fun removeMessagesListener() {
        if (::messagesListener.isInitialized) {
            messagesListener.remove()
        }
    }

    /**
     * opens image folder on mobile phone
     */
    private fun openImageFolder (userMessageId: String, messageId: String){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }


    /**
     * func to load chats done when a chat is opened ordered by date
     */
    private fun setupMessagesListener(messageId: String) {

        val messagesCollection = firestore.collection("chats").document(messageId).collection("messages")

        // Get the current message's timestamp
        val currentMessageTimestamp = Date().time

        messagesListener = messagesCollection.orderBy("date").endAt(currentMessageTimestamp) // order messages by date
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
     * func created to load the message as it is sent
     */

    private fun setRealTimeMessage(messageId: String) {

        val messagesCollection = firestore.collection("chats").document(messageId).collection("messages")
        val currentMessageTimestamp = Date().time
        messagesListener = messagesCollection.orderBy("date").startAt(currentMessageTimestamp)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && !snapshot.isEmpty) {
                    for (document in snapshot.documents) {
                        val chatMessage = document.toObject(ChatMessage::class.java)
                        if (chatMessage != null) {
                            if (!chatMessage.userId.contains(auth.currentUser?.uid)) {
                                // Only add the message if the sender is not the current user
                                messagesList.add(chatMessage)
                            }
                        }
                    }
                    messagesList.sortBy { it.date }
                    messagesAdapter.notifyDataSetChanged()
                    messagesRecyclerView.scrollToPosition(messagesList.size - 1)
                }
            }

    }

    /**
     * func that stores the messages in firebase
     */

    private fun sendMessage(userMessageId: String, messageId: String){
        val messageBox = findViewById<EditText>(R.id.messageBox)
        val messageText = messageBox.text.toString()

        if (selectedImageUri != null || messageText.isNotEmpty()) {
            val currentUserId = auth.currentUser?.uid.orEmpty()

            if (selectedImageUri != null && userMessageId != null && messageId != null) { // para imagens
                Log.d("MessageAdpater", "Entrei no send message")
                uploadImageAndSendMessage(userMessageId, messageId, selectedImageUri!!)

            } else if (messageText.isNotEmpty()) { // textos

                // User sent a text message
                val message = ChatMessage(userMessageId, messageId, listOf(currentUserId), Date().time, messageText, MessageType.TEXT.value, emptyList())

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
                        Toast.makeText(applicationContext, "Message sent successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Log.d("username", "Error sending message: $it")
                        Toast.makeText(
                            applicationContext,
                            "Error sending message",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        } else {
            Toast.makeText(applicationContext, "Message is empty", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * Upload Image to firebase firestore and send the image
     */

    private fun uploadImageAndSendMessage(userMessageId: String, messageId: String, imageUri: Uri) {
        val currentUserId = auth.currentUser?.uid.orEmpty()
        val messagesCollection = firestore.collection("chats").document(messageId).collection("messages")

        // Convert the image URI to a byte array
        val imageStream = contentResolver.openInputStream(imageUri)
        val imageBytes = imageStream?.readBytes()

        if (imageBytes != null) {
            // Create a message object with image data
            val message = ChatMessage(userMessageId, messageId, listOf(currentUserId), Date().time,imageUri.toString(), MessageType.IMAGE.value, emptyList())
            Log.d("MessageAdapter",imageUri.toString())


            // Upload the image to Firebase Storage
            val storageRef = FirebaseStorage.getInstance().reference.child("messages/${messageId}/${System.currentTimeMillis()}.jpg")
            val uploadTask = storageRef.putBytes(imageBytes)

            uploadTask.addOnSuccessListener { taskSnapshot ->
                // Get the download URL of the uploaded image
                val downloadUrl = taskSnapshot.storage.downloadUrl
                Log.d("MessageAdapter", downloadUrl.toString())
                // Add the message to Firestore with the download URL
                downloadUrl.addOnSuccessListener { url ->

                    messagesCollection.add(message)
                        .addOnSuccessListener { documentReference ->
                            // Message has been successfully added to Firestore, update UI
                            val messageId = documentReference.id
                            message.id = messageId

                            // Add the new message to the messagesList
                            messagesList.add(message)

                            // Notify the adapter that a new item has been inserted
                            messagesAdapter.notifyItemInserted(messagesList.size - 1)

                            // Clear the selectedImageUri after sending the message
                            selectedImageUri = null

                            messagesRecyclerView.scrollToPosition(messagesList.size - 1)

                            Log.d("username", "Image sent successfully")
                            Toast.makeText(applicationContext, "Image sent successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Log.d("username", "Failed to send image: ${it.message}")
                            Toast.makeText(applicationContext, "Failed to send image: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri  = data.data!!

            Log.d("MessageAdapter", "Selected Image Uri: $selectedImageUri ")

            // Call the sendMessage function with the selected image only if selectedImageUri is not null
            if (selectedImageUri != null) {
                val userMessageId = intent.getStringExtra("userId")
                val messageId = intent.getStringExtra("chatId")

                if (userMessageId != null && messageId != null) {
                    sendMessage(userMessageId, messageId)
                }
            }
        }
    }
}









