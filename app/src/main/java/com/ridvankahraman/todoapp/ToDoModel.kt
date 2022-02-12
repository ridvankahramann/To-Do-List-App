package com.ridvankahraman.todoapp

data class ToDoModel(
    val ID:String,
    var title:String,
    var details:String?,
    var imageUrl:String?,
    var done:Boolean?
)