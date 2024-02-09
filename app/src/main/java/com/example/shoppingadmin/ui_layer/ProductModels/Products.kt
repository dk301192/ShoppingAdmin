package com.example.shoppingadmin.UI.ui_layer.ProductModels


data class Products(
    var productDisplayImage: String?=null,
    var productDisplayImages: List<String>?=null,
    var productName: String?=null,
    var productDes: String?=null,
    var productPrice: Long?=null,
    var productDiscountPercent: Long?=null,
    var productSize: List<String>?=null,
    var productColor: List<ProductColor>?=null,
)

data class ProductColor(var colorName: String?=null, var colorCode: Int?=null)