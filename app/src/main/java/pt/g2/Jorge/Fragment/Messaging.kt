package pt.g2.Jorge.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import pt.g2.Jorge.Adapters.MessageAdapter
import pt.g2.Jorge.Adapters.MessageModal
import pt.g2.Jorge.R
import java.util.Calendar

class Messaging : Fragment() {
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var sendMessageEditText: EditText
    private lateinit var sendMessageButton: FloatingActionButton
    private lateinit var fstore: FirebaseFirestore
    private lateinit var fauth: FirebaseAuth
    private lateinit var messageLayoutManager: RecyclerView.LayoutManager
    private lateinit var messageAdapter: MessageAdapter
    private var db: DocumentReference = FirebaseFirestore.getInstance().document("chats/defaultChatId/messages/newMessage")
    private lateinit var userid: String
    private val messageInfo = arrayListOf<MessageModal>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_messaging, container, false)
        fstore = FirebaseFirestore.getInstance()
        fauth = FirebaseAuth.getInstance()
        userid = fauth.currentUser?.uid.toString()
        messageRecyclerView = view.findViewById(R.id.messageRecyclerView)
        sendMessageButton = view.findViewById(R.id.btSendMessage)
        sendMessageEditText = view.findViewById(R.id.etSendMessage)
        messageLayoutManager = LinearLayoutManager(requireContext())
        val adapterContext = requireContext()
        messageAdapter = MessageAdapter(adapterContext, messageInfo)
        messageRecyclerView.adapter = messageAdapter
        messageRecyclerView.layoutManager = messageLayoutManager

        // Initialize db with a default reference
        db = FirebaseFirestore.getInstance().document("chats/defaultChatId/messages/defaultMessageId")

        fstore.collection("chats").whereArrayContains("uids", userid).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.d("onError", "Error in fetching Data")
            } else {
                val list = snapshot?.documents
                if (list != null) {
                    for (doc in list) {
                        // No need to reassign db inside the loop
                        fstore.collection("chats").document(doc.id)
                            .collection("message").orderBy("id", Query.Direction.ASCENDING).addSnapshotListener { snapshot, exception ->
                                if (snapshot != null) {
                                    if (!snapshot.isEmpty) {
                                        messageInfo.clear()
                                        val list = snapshot.documents
                                        for (document in list) {
                                            val obj = MessageModal(
                                                document.getString("messageSender").toString(),
                                                document.getString("message").toString(),
                                                document.getString("messageTime").toString()
                                            )
                                            messageInfo.add(obj)
                                        }
                                        messageAdapter.notifyDataSetChanged()
                                        messageRecyclerView.scrollToPosition(list.size - 1)
                                    }
                                }
                            }
                    }
                }
            }
        }

        sendMessageButton.setOnClickListener {
            sendMessage()
        }
        return view
    }

    private fun sendMessage() {
        val message = sendMessageEditText.text.toString()
        if (TextUtils.isEmpty(message)) {
            sendMessageEditText.error = "Enter some Message to send"
        } else {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val timeStamp = "$hour:$minute"
            val messageObject = mutableMapOf<String, Any>().also {
                it["message"] = message
                it["messageId"] = 1
                it["messageSender"] = userid
                it["messageTime"] = timeStamp

                Log.d("Mensagem", "aqui")
            }

        }
    }
}
