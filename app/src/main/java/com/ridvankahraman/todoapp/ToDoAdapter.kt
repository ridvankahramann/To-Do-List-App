package com.ridvankahraman.todoapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso

open class ToDoAdapter(context: Context, toDoList: MutableList<ToDoModel>) : BaseAdapter() {
    private val inflater:LayoutInflater = LayoutInflater.from(context)
    private var itemList = toDoList
    private var updateAndDelete:UpdateAndDelete = context as UpdateAndDelete
    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(p0: Int): Any {
        return itemList.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val ID:String = itemList.get(p0).ID
        val title:String = itemList.get(p0).title
        val details: String? = itemList.get(p0).details
        val imageUrl: String? = itemList.get(p0).imageUrl
        val done:Boolean = itemList.get(p0).done as Boolean
        val view:View
        val viewHolder:ListViewHolder
        if (p1 == null){
            view = inflater.inflate(R.layout.row_itemslayout, p2, false)
            viewHolder = ListViewHolder(view)
            view.tag = viewHolder
        }else{
            view = p1
            viewHolder = view.tag as ListViewHolder
        }
        viewHolder.textView?.text = title
        viewHolder.checkBox?.isChecked = done
        Picasso.get().load(imageUrl).fit().centerCrop().into(viewHolder.row_image)

        if(viewHolder.checkBox?.isChecked == true){
            viewHolder.textView?.paintFlags = viewHolder.textView?.paintFlags?.or(Paint.STRIKE_THRU_TEXT_FLAG)!!
        }else{
            viewHolder.textView?.paintFlags = viewHolder.textView?.paintFlags?.and(Paint.STRIKE_THRU_TEXT_FLAG.inv())!!
        }

        viewHolder.checkBox?.setOnClickListener {
            updateAndDelete.modifyItem(ID,!done)
        }
        viewHolder.isDeleted?.setOnClickListener {
            if (imageUrl != null) {
                updateAndDelete.onItemDelete(ID,imageUrl)
            }
        }
        view.setOnClickListener {
            val context = viewHolder.textView?.context
            val intent = Intent(context,NewTodoEdit::class.java)
            intent.putExtra("ID",ID)
            intent.putExtra("title",title)
            intent.putExtra("details",details)
            intent.putExtra("imageUrl",imageUrl)
            intent.putExtra("done",done)
            context?.startActivity(intent)
        }

        return view
    }

    class ListViewHolder(row:View?) {
        val textView: TextView? = row?.findViewById(R.id.textView)
        val checkBox: CheckBox? = row?.findViewById(R.id.checkbox)
        val row_image: ImageView? = row?.findViewById(R.id.row_image)
        val isDeleted: Button? = row?.findViewById(R.id.isDelete)
    }

}