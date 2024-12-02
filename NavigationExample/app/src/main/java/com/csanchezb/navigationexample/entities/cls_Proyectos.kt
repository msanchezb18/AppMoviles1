package com.csanchezb.navigationexample.entities

class cls_Proyectos {
    var titulo: String = ""
    var area: String = ""
    var correo: String = ""
    var descripcion: String = ""
    var pdfLink: String = ""

    constructor() {}

    constructor(titulo: String, area: String, correo: String, descripcion: String, pdfLink: String){
        this.titulo = titulo
        this.area = area
        this.correo = correo
        this.descripcion = descripcion
        this.pdfLink = pdfLink
    }


}