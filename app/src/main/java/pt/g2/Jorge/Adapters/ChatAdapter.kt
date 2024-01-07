package pt.g2.Jorge.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import pt.g2.Jorge.R
import java.io.Serializable

data class chat(
    var id: String,
    var userIds: List<String>,
    var date: Double,
    var name: String
)
