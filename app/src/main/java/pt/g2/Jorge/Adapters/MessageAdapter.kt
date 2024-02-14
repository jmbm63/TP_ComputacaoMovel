package pt.g2.Jorge.Adapters

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import pt.g2.Jorge.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.bumptech.glide.Glide


data class ChatMessage(
    var id: String = "",
    var chatId: String = "",
    var userId: String = "",
    var date: Long = 0,
    var body: String = "",
    var messagetype: Int = 0, // 1 texto, 2 imagens seq
    var readby: List<Byte> = emptyList()
)

enum class MessageType(val value: Int) {
    TEXT(1),
    IMAGE(2),
    VIDEO(3),
    LINK(4),
    GIF(5),
    AUDIO(6)
}


// recycler view adapter
class MessageAdapter(private val messages: List<ChatMessage>, private val userId: String) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val MESSAGE_TYPE_receiver = 0
    private val MESSAGE_TYPE_send = 1
    var firebaseUser: FirebaseUser? = null


    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.textView_message_text)
        val timestamp: TextView = itemView.findViewById(R.id.textView_message_time)
        val imageView: ImageView = itemView.findViewById(R.id.imageView_message_image)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        if (viewType == MESSAGE_TYPE_send) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.text_message_send, parent, false)
            return MessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.text_message_receiver, parent, false)
            return MessageViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser =FirebaseAuth.getInstance().currentUser

        if(messages[position].userId == firebaseUser!!.uid){
            return MESSAGE_TYPE_send
        }else{
            return MESSAGE_TYPE_receiver
        }

    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {

        val sortedMessages = messages.sortedBy { it.date }
        val message = sortedMessages[position]

        // Display text message if it's not an image message
        if (message.messagetype == MessageType.TEXT.value) {
            holder.messageText.text = message.body
            holder.messageText.visibility = View.VISIBLE
            holder.imageView.visibility = View.GONE

        } else  if (message.messagetype == MessageType.IMAGE.value) {

            // If it's an image message, load the image using Glide
            val imageUri = Uri.parse(message.body)
            Glide.with(holder.itemView.context)
                .load(imageUri)
                .fitCenter()
                .into(holder.imageView)

            // Hide the text view
            holder.messageText.visibility = View.GONE
            holder.imageView.visibility = View.VISIBLE

        }

        holder.timestamp.text = formatTimestamp(message.date)
    }


    override fun getItemCount(): Int {
        return messages.size
    }

    private fun formatTimestamp(timestamp: Long): String {
        return SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
    }
}