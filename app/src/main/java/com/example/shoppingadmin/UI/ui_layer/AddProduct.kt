package com.example.shoppingadmin.UI.ui_layer

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator
import com.denzcoskun.imageslider.models.SlideModel
import com.example.shoppingadmin.R
import com.example.shoppingadmin.UI.common.INVISIBLE
import com.example.shoppingadmin.UI.common.PRODUCT_IMAGES_FOLDER_PATH
import com.example.shoppingadmin.UI.common.VISIBLE
import com.example.shoppingadmin.UI.common.uploafImage
import com.example.shoppingadmin.UI.ui_layer.ProductModels.Adapter.ColorAdapter
import com.example.shoppingadmin.UI.ui_layer.ProductModels.ProductColor
import com.example.shoppingadmin.UI.ui_layer.ProductModels.Products
import com.example.shoppingadmin.databinding.ActivityAddProductBinding
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.github.dhaval2404.colorpicker.util.setVisibility
import com.github.guilhe.views.CircularProgressView

class AddProduct : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var circularProgressbar: CircularProgressView
    private val binding: ActivityAddProductBinding by lazy {
        ActivityAddProductBinding.inflate(layoutInflater)
    }

    private var imageList = ArrayList<SlideModel>()
    private var colors = arrayListOf<ProductColor>()
    lateinit var colorAdapter: ColorAdapter
    private var colorProduct = ProductColor()
    private var sizes = arrayOf<String?>("S", "M", "L", "XL", "XXL", "XXXL")
    private lateinit var dialog: Dialog
    var product =Products()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        circularProgressbar = binding.circularProgressBar

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
            saveProduct.setOnClickListener {
                Toast.makeText(
                    applicationContext, "Save Item Successfully!",
                    Toast.LENGTH_LONG
                )
                    .show()

            }
            showDialog()
        }

        val spin = findViewById<Spinner>(R.id.spinner)
        spin.onItemSelectedListener = this@AddProduct

        // Create the instance of ArrayAdapter
        // having the list of courses
        val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_item,
            sizes
        )

        ad.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spin.adapter = ad

        binding.saveProduct.setOnClickListener {
            product.productDisplayImage
        }

    }

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
            circularProgressbar.setProgress(0.0F)
            uploafImage(PRODUCT_IMAGES_FOLDER_PATH,uri,
                {result:Boolean,fileUrl:String->

                    if(result){
                        product.productDisplayImage=fileUrl
                        binding.showDisplayImg.setImageURI(uri)
                        binding.showDisplayImg.visibility = View.VISIBLE
                        Toast.makeText(this, "Image found Successfully!", Toast.LENGTH_SHORT).show()
                        dismissCustomDialog()

                    }
                    else
                    {
                        Toast.makeText(this, "Image Not found !", Toast.LENGTH_SHORT).show()
                        dismissCustomDialog()
                    }
                    println("")
                },
                {byteTrassferred:Long,tottalBytes:Long->
                    var percentage = (byteTrassferred.toDouble() / tottalBytes) * 100

                    circularProgressbar.setProgress(percentage.toFloat())

                })
            showCustomDialog()

        } else {

            Toast.makeText(this, "Image Not found !", Toast.LENGTH_SHORT).show()
            dismissCustomDialog()
        }
    }

    // Registers a photo picker activity launcher in single-select mode.
    val pickMedia2 = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            addImgInList(uri)
            binding.imageSlider.visibility = View.VISIBLE
            Toast.makeText(this, "Image found Successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Image Not found !", Toast.LENGTH_SHORT).show()
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
    }
    private fun dismissCustomDialog()
    {
        circularProgressbar.visibility = View.INVISIBLE
    }
}