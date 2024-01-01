package pt.g2.Jorge.Chats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.g2.Jorge.Fragment.Messaging
import pt.g2.Jorge.R

/**
 * This activity is used to hold the Messaging fragment
 */
class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, Messaging())
            .commit()
    }

}
