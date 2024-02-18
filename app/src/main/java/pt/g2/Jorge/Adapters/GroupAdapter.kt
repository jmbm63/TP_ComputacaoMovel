package pt.g2.Jorge.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import pt.g2.Jorge.R

class DataModel(var id: String, var name: String?, var checked: Boolean)

class CustomAdapter(
    private val dataSet: ArrayList<DataModel>,
    private val mContext: Context,
    private var selectedUserIds: ArrayList<String> = ArrayList()
) : ArrayAdapter<DataModel>(mContext, R.layout.activity_group, dataSet) {

    private class ViewHolder {
        lateinit var txtName: TextView
        lateinit var checkBox: CheckBox
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        val view: View

        val item: DataModel? = getItem(position)

        if (convertView == null) {
            viewHolder = ViewHolder()
            view = LayoutInflater.from(parent.context).inflate(R.layout.checkbox, parent, false)
            viewHolder.txtName = view.findViewById(R.id.txtName)
            viewHolder.checkBox = view.findViewById(R.id.checkBox)

            view.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            view = convertView
        }

        item?.let { nonNullItem ->
            viewHolder.txtName.text = nonNullItem.name
            viewHolder.checkBox.isChecked = nonNullItem.checked

            viewHolder.checkBox.setOnCheckedChangeListener { _, isChecked ->
                nonNullItem.checked = isChecked
                notifyDataSetChanged()

                // Toggle selection of user ID
                val userId = nonNullItem.id
                if (isChecked) {
                    // If checked, add to selectedUserIds
                    selectedUserIds.add(userId)
                } else {
                    // If unchecked, remove from selectedUserIds
                    selectedUserIds.remove(userId)
                }
            }
        }

        return view
    }

}
