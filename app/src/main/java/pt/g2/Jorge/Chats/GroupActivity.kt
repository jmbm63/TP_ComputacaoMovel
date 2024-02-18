package pt.g2.Jorge.Chats

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pt.g2.Jorge.Adapters.CustomAdapter
import pt.g2.Jorge.Adapters.DataModel
import pt.g2.Jorge.Adapters.chat
import pt.g2.Jorge.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GroupActivity : AppCompatActivity() {
    private lateinit var listView: ListView

    private lateinit var adapter: CustomAdapter
    private val dataModel: ArrayList<DataModel> = ArrayList()
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val selectedUserIds: ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        listView = findViewById(R.id.list_view_1)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        // Fetch user data from Firestore and populate the list


        adapter = CustomAdapter(dataModel, applicationContext)
        listView.adapter = adapter


        showListUsers()

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedItem: DataModel = dataModel[position]
            selectedItem.checked = !selectedItem.checked
            adapter.notifyDataSetChanged()

            // Toggle selection of user ID
            val userId = getUserId(position)
            if (selectedItem.checked) {
                // If checked, add to selectedUserIds
                selectedUserIds.add(userId)
            } else {
                // If unchecked, remove from selectedUserIds
                selectedUserIds.remove(userId)
            }
        }


        val saveButton = findViewById<Button>(R.id.buttonSave)

        saveButton.setOnClickListener {
            // Handle Save button click
            val selectedUserNames = dataModel.filter { it.checked }.map { it.name ?: "" }
            val selectedUserIds = dataModel.filter { it.checked }.map { it.id ?: "" }

            // Check if any user is selected
            if (selectedUserIds.isEmpty()) {
                Toast.makeText(
                    this,
                    "Select at least one user to create a group chat",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                // Store the chat in Firebase Firestore
                storeChat(selectedUserNames, selectedUserIds)
            }
        }
    }


    private fun getUserId(position: Int): String {
        return dataModel[position].id // Assuming the user ID is stored in the DataModel's id field
    }

    private fun showListUsers() {
        firestore.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                dataModel.clear()

                for (document in documents) {
                    val userId = document.getString("userId")
                    val userName = document.getString("userName")
                    if (userId != null && userName != null) {
                        if(!auth.currentUser?.uid.equals(userId)) {
                            val data = DataModel(userId, userName, false) // Assign userId as the id
                            dataModel.add(data)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting documents: ", exception)
                Toast.makeText(this, "Failed to fetch users", Toast.LENGTH_SHORT).show()
            }
    }


    private fun generateChatId(length: Int = 20): String {
        val alphaNumeric = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return alphaNumeric.shuffled().take(length).joinToString("")
    }

    private fun openChatActivity(selectedUserName: List<String>, userId: List<List<String>>, chatId: String?) {
        //Log.d("username", "Entrei")
        //Log.d("username", "ChatID: $chatId")

        val chatActivityIntent = Intent(this, ChatActivity::class.java)
        chatActivityIntent.putStringArrayListExtra("userNames", ArrayList(selectedUserName))
        Log.d("Grupos", ArrayList(selectedUserName).toString())
        chatActivityIntent.putStringArrayListExtra("userIds", ArrayList(selectedUserIds))
        Log.d("Grupos", ArrayList(selectedUserIds).toString())
        chatActivityIntent.putExtra("chatId", chatId)
        startActivity(chatActivityIntent)

    }

    private fun storeChat(selectedUserNames: List<String>, selectedUserIds: List<String>) {
        // Generate chat ID
        val chatId = generateChatId()

        // Create a new chat document
        val newChat = chat(
            id = chatId,
            userIds = selectedUserIds + auth.currentUser!!.uid, // add the selected people and current user
            date = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date().time),
            name = "Group Chat",
        )

        // Add the new chat document to the "chats" collection
        firestore.collection("chats").document(chatId).set(newChat)
            .addOnSuccessListener {
                Log.d("username", "Chat document created successfully")
                finish()// go back to chatlist
            }
            .addOnFailureListener { e ->
                Log.d("username", "Error creating chat document", e)
            }
    }
}

