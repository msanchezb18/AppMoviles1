package com.csanchezb.navigationexample.entities

class cls_Galeria {
    var tittle: String = ""
    var descrip: String = ""
    var imagenesUrl: List<String> = emptyList() // Inicialización segura

    constructor() {}

    constructor(tittle: String, descrip: String, imagenesUrl : List<String>){
        this.tittle = tittle
        this.descrip = descrip
        this.imagenesUrl = imagenesUrl

    }



}