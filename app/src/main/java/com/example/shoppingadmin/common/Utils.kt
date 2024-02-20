package com.example.shoppingadmin.common


import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

const val PRODUCT_IMAGES_FOLDER_PATH = "Product_images"
const val PRODUCT_DISPLAY_IMAGES_FOLDER_PATH = "Product_Display_images"
const val PRODUCT_PATH = "Product_path"


fun uploadImage(
 path: String,
 uri: Uri,
 function: (isSuccessful: Boolean, fileUrl: String) -> Unit
 ,updateProgressBar:(bytesTransferred:Long,totalByteCount:Long)->Unit) {

 var uploadTask= Firebase.storage.reference.child("$path/${UUID.randomUUID()}.jpg").putFile(uri)

 uploadTask.addOnCompleteListener {
  it.result.storage.downloadUrl.addOnSuccessListener {
   function(true, it.toString())
  }

 }
 uploadTask.addOnProgressListener {
  var percentage = (it.bytesTransferred.toDouble() / it.totalByteCount) * 100
  Log.d("uploadImage", " ${percentage} ")

  updateProgressBar((it.bytesTransferred),(it.totalByteCount))

 }
  .addOnFailureListener {

  }


}
sealed class Category(var name:String)
{
 object dress : Category("dress")
 object jump_suit :Category("Jump Suit")
 object shirt : Category("Shirt")
 object bottoms : Category("Bottoms")
 object tops : Category("Tops")
}