package com.csanchezb.navigationexample.entities

class cls_Proyectos {
    var titulo: String = ""
    var area: String = ""
    var usuario: String = ""
    var descripcion: String = ""
    var pdfLink: String = ""

    constructor() {}

    constructor(titulo: String, area: String, usuario: String, descripcion: String, pdfLink: String){
        this.titulo = titulo
        this.area = area
        this.usuario = usuario
        this.descripcion = descripcion
        this.pdfLink = pdfLink
    }


}