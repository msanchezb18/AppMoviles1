package com.csanchezb.navigationexample.entities

class cls_Galeria {
    var tittle: String = ""
    var descrip: String = ""
    lateinit var imagenesUrl: List<String>

    constructor() {}

    constructor(tittle: String, descrip: String, imagenesUrl : List<String>){
        this.tittle = tittle
        this.descrip = descrip
        this.imagenesUrl = imagenesUrl

    }



}