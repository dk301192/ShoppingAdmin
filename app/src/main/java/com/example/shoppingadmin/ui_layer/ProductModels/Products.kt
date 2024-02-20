package com.example.shoppingadmin.ui_layer.ProductModels


data class Products(
    var productDisplayImage: String?=null,
    var productDisplayImages: ArrayList<String>?= arrayListOf<String>(),
    var productName: String?=null,
    var productDes: String?=null,
    var productPrice: Long?=null,
    var productDiscountPercent: Long?=null,
    var productSize: List<String>?=null,
    var productColor: List<ProductColor>?=null,
    var category : String?=null,
    var addedTimeStamp :Long?=null
)

data class ProductColor(var colorName: String?=null, var colorCode: Int?=null)