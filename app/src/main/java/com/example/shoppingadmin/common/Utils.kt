package com.example.shoppingadmin.UI.common

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.net.URI
import java.util.UUID


const val PRODUCT_IMAGES_FOLDER_PATH="Product_images"
const val VISIBLE =1
const val INVISIBLE=0

fun uploafImage(path:String,uri:Uri,function: (result:Boolean,fileUrl:String)->Unit,updateProgressBar:(bytesTransferred:Long,totalByteCount:Long)->Unit)
{
 var uploadTask = Firebase.storage.reference.child("$path/{${UUID.randomUUID()}}.jpg").putFile(uri)
 uploadTask.addOnProgressListener {

  updateProgressBar((it.bytesTransferred),(it.totalByteCount))
 }
uploadTask.addOnCompleteListener {
 it.result.storage.downloadUrl.addOnCompleteListener {
  function(true,it.toString())
 }
 }
uploadTask.addOnFailureListener {
 function(false,it.toString())
}



}

