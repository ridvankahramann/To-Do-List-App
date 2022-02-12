package com.ridvankahraman.todoapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

@Suppress("DEPRECATION")
open class NewTodoEdit : AppCompatActivity() {
    lateinit var database: DatabaseReference
    lateinit var ImageUri: Uri
    lateinit var imageView: ImageView
    lateinit var imageUrl:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_todo_edit)
        database = FirebaseDatabase.getInstance().reference

        val backbtn = findViewById<Button>(R.id.backbtn)
        val titletext = findViewById<EditText>(R.id.titletext)
        val detailstext = findViewById<EditText>(R.id.detailstext)
        val savebtn = findViewById<Button>(R.id.savebtn)
        val choosefile = findViewById<Button>(R.id.choosefile)
        imageView = findViewById(R.id.imageView)

        val ID = intent.getStringExtra("ID").toString()
        val title = intent.getStringExtra("title").toString()
        val details = intent.getStringExtra("details").toString()
        if (ID == "null") {
            savebtn.setText("Save")
        } else {
            titletext.setText(title)
            detailstext.setText(details)
            Picasso.get().load(intent.getStringExtra("imageUrl").toString()).fit().centerCrop().into(imageView)
            savebtn.setText("Update")
        }

        choosefile.setOnClickListener {
            selectImage()
        }
        savebtn.setOnClickListener {
            if(ID == "null"){
                val newItemData = database.child("todo").push()
                val storageReference = FirebaseStorage.getInstance().getReference("images/${newItemData.key.toString()}")
                val upload = storageReference.putFile(ImageUri)
                val urlTask = upload.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    storageReference.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val todomodel = ToDoModel(newItemData.key.toString(),titletext.text.toString(),detailstext.text.toString(),task.result.toString(),false)
                        newItemData.setValue(todomodel)
                    }
                }
                val intent = Intent(this,MainActivity::class.java)
                this.startActivity(intent)
            }else{
                val storageReference = FirebaseStorage.getInstance().getReference("images/${ID}")
                val upload = storageReference.putFile(ImageUri)
                val urlTask = upload.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    storageReference.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        MainActivity().onUpdateItem(ID,titletext.text.toString(),detailstext.text.toString(),task.result.toString())
                    }
                }
                val intent = Intent(this,MainActivity::class.java)
                this.startActivity(intent)
            }
        }
        backbtn.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            this.startActivity(intent)
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && resultCode == RESULT_OK){
            ImageUri = data?.getData()!!
            imageView.setImageURI(ImageUri)
        }
    }
}