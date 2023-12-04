package pt.g2.Jorge

import android.content.Context
import android.widget.ArrayAdapter

class ChatAdapterr(context: Context, resource: Int, objects: MutableList<String>) :
    ArrayAdapter<String>(context, resource, objects)  {

        var mContext: Context
        var mValues: MutableList<String>
        var mResource: Int
        init {
            mContext = context
            mValues = objects
            mResource = resource
        }
}