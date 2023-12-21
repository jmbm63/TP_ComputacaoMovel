package pt.g2.Jorge.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import pt.g2.Jorge.R

class ChatAdapter(context: Context, resource: Int, objects: List<String>) :
    ArrayAdapter<String>(context, resource, objects)  {

        var mContext: Context
        var mValues: MutableList<String>
        var mResource: Int
        init {
            mContext = context
            mValues = objects.toMutableList()
            mResource = resource
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(mContext)
        val rowView = inflater.inflate(mResource,parent,false)
        val textView = rowView.findViewById<TextView>(R.id.chatList)
        //val imageView = rowView.findViewById<ImageView>(R.id.chatList)
        val value = mValues[position]

        val imageResource = when {
            value.startsWith("linux") -> android.R.drawable.star_on
            else -> android.R.drawable.star_off
        }

        textView.text = value
        //imageView.setImageResource(imageResource)
        return rowView
    }
    }