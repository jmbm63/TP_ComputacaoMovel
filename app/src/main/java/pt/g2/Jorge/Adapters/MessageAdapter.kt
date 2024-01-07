package pt.g2.Jorge.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import pt.g2.Jorge.R


data class Message (
    var id:String,
    var chatId: String,
    var userId: String,
    var date: Double,  // timestamp
    var body: String,
    var messageType: Int,
    var readby: List<String> // userids que ja leram a mensagem
)




//class MessageAdapter( val messageList : ArrayList<Message>): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>(){ }