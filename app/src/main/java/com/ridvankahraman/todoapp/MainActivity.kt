package com.ridvankahraman.todoapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

open class MainActivity : AppCompatActivity(),UpdateAndDelete {
    lateinit var  database: DatabaseReference
    var toDoList: MutableList<ToDoModel>? = null
    private lateinit var adapter: ToDoAdapter
    private var listViewItem: ListView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab = findViewById<View>(R.id.floatingActionButton) as FloatingActionButton
        listViewItem = findViewById(R.id.listView)

        database = FirebaseDatabase.getInstance().reference
        toDoList = mutableListOf<ToDoModel>()
        adapter = ToDoAdapter(this, toDoList!!)
        listViewItem!!.adapter = adapter

        fab.setOnClickListener {
            val intent = Intent(this,NewTodoEdit::class.java)
            this.startActivity(intent)
        }

        database.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "No Item Added",Toast.LENGTH_LONG).show()
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                toDoList!!.clear()
                addItemToList(snapshot)
            }
        })
    }

    private fun addItemToList(snapshot: DataSnapshot) {
        val row_image = findViewById<ImageView>(R.id.row_image)
        val items = snapshot.children.iterator()
        if(items.hasNext()){
            val toDoIndexedValue = items.next()
            val itemsIterator = toDoIndexedValue.children.iterator()

            while (itemsIterator.hasNext()){
                val currentItem = itemsIterator.next()
                val map = currentItem.value as HashMap<*, *>
                toDoList?.add(ToDoModel(currentItem.key as String, map["title"] as String, map["details"] as String, map["imageUrl"].toString(), map["done"] as Boolean?))
            }
            adapter = ToDoAdapter(this, toDoList!!)
            listViewItem!!.adapter = adapter
        }
        adapter.notifyDataSetChanged()
    }

    override fun modifyItem(ID: String, done: Boolean) {
        val itemReference = database.child("todo").child(ID)
        itemReference.child("done").setValue(done)
    }

    override fun onItemDelete(ID: String,imageUrl:String) {
        val itemReference = database.child("todo").child(ID)
        itemReference.removeValue()
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        storageReference.delete()
        adapter.notifyDataSetChanged()
    }

    override fun onUpdateItem(ID: String, title: String, details: String, imageUrl:String) {
        database = FirebaseDatabase.getInstance().reference
        val itemReference = database.child("todo").child(ID)
        itemReference.child("title").setValue(title)
        itemReference.child("details").setValue(details)
        itemReference.child("imageUrl").setValue(imageUrl)
    }
}