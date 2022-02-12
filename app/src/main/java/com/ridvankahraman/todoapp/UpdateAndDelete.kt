package com.ridvankahraman.todoapp

interface UpdateAndDelete{
    fun modifyItem(ID:String,done:Boolean)
    fun onItemDelete(ID: String,imageUrl: String)
    fun onUpdateItem(ID: String,title:String,details:String,imageUrl:String)
}