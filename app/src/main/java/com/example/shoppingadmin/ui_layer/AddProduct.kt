package com.example.shoppingadmin.ui_layer.Sheets

import android.app.Dialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.models.SlideModel
import com.example.shoppingadmin.R
import com.example.shoppingadmin.common.Category
import com.example.shoppingadmin.common.PRODUCT_DISPLAY_IMAGES_FOLDER_PATH
import com.example.shoppingadmin.common.PRODUCT_IMAGES_FOLDER_PATH
import com.example.shoppingadmin.common.PRODUCT_PATH
import com.example.shoppingadmin.common.uploadImage


import com.example.shoppingadmin.databinding.ActivityAddProductBinding
import com.example.shoppingadmin.ui_layer.ProductModels.ProductColor
import com.example.shoppingadmin.ui_layer.ProductModels.Products
import com.example.shoppingadmin.ui_layer.Sheets.Adapter.ColorAdapter
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.github.dhaval2404.colorpicker.util.setVisibility
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rejowan.cutetoast.CuteToast
import java.io.Console

class AddProduct : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private val categoriesList = ArrayList<String>()
    private val binding: ActivityAddProductBinding by lazy {
        ActivityAddProductBinding.inflate(layoutInflater)
    }
    private lateinit var circularProgressbar: ProgressBar

    private lateinit var saveProductProgressBar: ProgressBar
    var percentText : TextView? =null

    private var imageList = ArrayList<SlideModel>()
    private var colors = arrayListOf<ProductColor>()
    lateinit var colorAdapter: ColorAdapter
    private var colorProduct = ProductColor()
    private var sizes = arrayListOf("S", "M", "L", "XL", "XXL", "XXXL")
    private lateinit var dialog: Dialog
    var product = Products()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        circularProgressbar = binding.circularProgressBar
        saveProductProgressBar = binding.saveProductProgressBar

        percentText = binding.percent
         setCategoryList()
         setCategoryDropdown()
         setMulipleSizeSelectionDropDown()
        binding.apply {
            addDisplayImg.setOnClickListener {
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            addSubImg.setOnClickListener {
                // Launch the photo picker and let the user choose only images.
                pickMedia2.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            addProductColors.setOnClickListener {
                showColorPicker()
            }

            showDialog()

            binding.saveProduct.setOnClickListener {
                saveProductProgressBar.setVisibility(View.VISIBLE)
                product.productName= binding.productName.text.toString().trim()
                product.productPrice= binding.productPrice.text.toString().trim().toLong()
                product.productDes= binding.productDisp.text.toString().trim()
                product.productDiscountPercent= binding.productDiscount.text.toString().trim().toLong()
                product.productColor=colors
                product.addedTimeStamp=System.currentTimeMillis()
                product.category=spinner.selectedItem.toString()



                Log.d("PRODUCT", "onCreate: $product")
                FirebaseFirestore.getInstance().collection(PRODUCT_PATH).document().set(product).addOnCompleteListener {
                    if (it.isSuccessful){
                        saveProductProgressBar.setVisibility(View.GONE)

                        CuteToast.ct(this@AddProduct, "Product added SucessFully . . . ", CuteToast.LENGTH_SHORT, CuteToast.HAPPY, true).show();
                       // Toast.makeText(this@AddProduct, "Product added SucessFully . . . ", Toast.LENGTH_SHORT).show()
                    }else{
                        saveProductProgressBar.setVisibility(View.GONE)
                        CuteToast.ct(this@AddProduct, "${it.exception?.localizedMessage}", CuteToast.LENGTH_SHORT, CuteToast.ERROR, true).show();

                        //Toast.makeText(this@AddProduct, "${it.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()

                       // Log.d("Failed", "onCreate: ${it.exception?.localizedMessage}")
                    }
                }

            }
        }



    }

    private fun setMulipleSizeSelectionDropDown() {
        with(binding) {
            multiSelectSpinner.buildCheckedSpinner(sizes){ selectedPositionList, displayString ->

                if(displayString!=null && displayString!="") {
                    product.productSize = (displayString.split(",").toTypedArray()).toList()
                }

            }
        }    }

    private fun setCategoryList()  {
        categoriesList!!.add(Category.dress.name)
        categoriesList!!.add(Category.jump_suit.name)
        categoriesList!!.add(Category.bottoms.name)
        categoriesList!!.add((Category.tops.name))
        categoriesList!!.add(Category.shirt.name)

    }

    private fun setCategoryDropdown() {

          val spin = findViewById<Spinner>(R.id.spinner)
          spin.onItemSelectedListener = this@AddProduct

        // Create the instance of ArrayAdapter
        // having the list of courses
         val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
             this,
             android.R.layout.simple_spinner_item,
             categoriesList as List<Any?>
         )

         ad.setDropDownViewResource(
             android.R.layout.simple_spinner_dropdown_item
         )
         spin.adapter = ad    }

    private fun showDialog() {
        dialog = Dialog(this@AddProduct)
        dialog.setContentView(R.layout.color_dialog_box)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val saveColorBtn = dialog.findViewById<AppCompatButton>(R.id.saveColor)
        saveColorBtn.setOnClickListener {
            val colorName = dialog.findViewById<EditText>(R.id.edColor)
            val name = colorName.text.toString()
            colorProduct.colorName = name
            colors.add(colorProduct)
            colorProduct = ProductColor()
            val setColor = dialog.findViewById<View>(R.id.setColor)
            colorAdapter(colors)
            Log.d("ColorLists", "onCreate : $colors")
            dialog.dismiss()
        }
    }

    private fun showColorPicker() {
        // Kotlin Code
        MaterialColorPickerDialog
            .Builder(this@AddProduct)                            // Pass Activity Instance
            .setTitle("Pick Theme")                // Default "Choose Color"
            .setColorShape(ColorShape.CIRCLE)    // Default ColorShape.CIRCLE
            .setColorSwatch(ColorSwatch._700)    // Default ColorSwatch._500
            .setDefaultColor(R.color.appColor)        // Pass Default Color
            .setColorListener { color, colorHex ->
                // Handle Color Selection
                // Set background color to the selected color
                colorProduct.colorCode = color
                val setColor = dialog.findViewById<View>(R.id.setColor)
                setColor.setBackgroundColor(color)
                dialog.show()
            }
            .show()
    }

    private fun colorAdapter(colors: ArrayList<ProductColor>) {
        colorAdapter = ColorAdapter(colors, this)
        binding.rvColor.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvColor.adapter = colorAdapter
    }


    private fun addImgInList(uri: Uri) {
        imageList.add(SlideModel(uri.toString()))
        binding.imageSlider.setImageList(imageList)
    }


    // Registers a photo picker activity launcher in single-select mode.
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            showCustomDialog()
            circularProgressbar.setProgress(0,true)

            uploadImage(PRODUCT_IMAGES_FOLDER_PATH, uri, { it, imageUrl ->
                if (it) {
                    product.productDisplayImage = imageUrl
                    binding.showDisplayImg.setImageURI(uri)
                    //  product.productDisplayImages
                    binding.showDisplayImg.visibility = View.VISIBLE
                    dismissCustomDialog()
                    CuteToast.ct(this@AddProduct, "Image found Successfully! ", CuteToast.LENGTH_SHORT, CuteToast.HAPPY, true).show();

                  //  Toast.makeText(this, "Image found Successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    dismissCustomDialog()
                    CuteToast.ct(this@AddProduct, "Image not Successfully! ", CuteToast.LENGTH_SHORT, CuteToast.SAD, true).show();

                   // Toast.makeText(this, "Image Not found !", Toast.LENGTH_SHORT).show()

                }
            },{
                    byteTrassferred:Long,tottalBytes:Long->
                var percentage = (byteTrassferred.toDouble() / tottalBytes) * 100

                circularProgressbar.setProgress(percentage.toInt(),true)
                percentText?.text = "${percentage.toInt()}%"

            })

        } else {
            dismissCustomDialog()
            CuteToast.ct(this@AddProduct, "Image not found! ", CuteToast.LENGTH_SHORT, CuteToast.HAPPY, true).show();

           // Toast.makeText(this, "Image Not found !", Toast.LENGTH_SHORT).show()
        }
    }

    // Registers a photo picker activity launcher in single-select mode.
    val pickMedia2 = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        uri.forEach {
                uri->
            circularProgressbar.progress = 0
            showCustomDialog()

            uploadImage(PRODUCT_DISPLAY_IMAGES_FOLDER_PATH, uri,{ it, imageUrl ->
                if (it) {

                    addImgInList(uri)
                    product.productDisplayImages?.add(imageUrl)
                    binding.imageSlider.visibility = View.VISIBLE
                    CuteToast.ct(this@AddProduct, "Image found Successfully! ", CuteToast.LENGTH_SHORT, CuteToast.HAPPY, true).show();

                    // Toast.makeText(this, "Image found Successfully!", Toast.LENGTH_SHORT).show()
                    dismissCustomDialog()
                } else {
                    dismissCustomDialog()
                    CuteToast.ct(this@AddProduct, "Image not founr! ", CuteToast.LENGTH_SHORT, CuteToast.HAPPY, true).show();

                    //Toast.makeText(this, "Image Not found !", Toast.LENGTH_SHORT).show()

                }
            },
                {
                        byteTrassferred:Long,tottalBytes:Long->
                    var percentage = (byteTrassferred.toDouble() / tottalBytes) * 100

                    circularProgressbar.progress = percentage.toInt()
                    percentText?.text = "${percentage.toInt()}%"

                })

        }

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        Toast.makeText(
//            applicationContext,
//            sizes[position],
//            Toast.LENGTH_LONG
//        )
//            .show()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun showCustomDialog()
    {

        circularProgressbar.visibility = View.VISIBLE
        percentText?.visibility = View.VISIBLE
    }
    private fun dismissCustomDialog()
    {
        circularProgressbar.visibility = View.GONE
        percentText?.visibility = View.GONE

    }
}