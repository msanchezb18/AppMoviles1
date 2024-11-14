package com.csanchezb.a12_firebaseaccess.entities

class cls_Customer {
    var CustomerID: String = ""
    var CompanyName: String = ""
    var ContactName: String = ""
    var ContactTitle: String = ""
    var Address: String = ""
    var City: String = ""
    var Region: String = ""
    var PostalCode: String = ""
    var Country: String = ""
    var Phone: String = ""
    var Fax: String = ""

    constructor() {}

    constructor(
        CustomerID: String,
        CompanyName: String,
        ContactName: String,
        ContactTitle: String,
        Address: String,
        City: String,
        Region: String,
        PostalCode: String,
        Country: String,
        Phone: String,
        Fax: String
    ) {
        this.CustomerID = CustomerID
        this.CompanyName = CompanyName
        this.ContactName = ContactName
        this.ContactTitle = ContactTitle
        this.Address = Address
        this.City = City
        this.Region = Region
        this.PostalCode = PostalCode
        this.Country = Country
        this.Phone = Phone
        this.Fax = Fax
    }
}
