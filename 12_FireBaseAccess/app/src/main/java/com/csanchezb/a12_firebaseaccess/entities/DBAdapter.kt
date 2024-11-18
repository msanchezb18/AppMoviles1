package com.csanchezb.a12_firebaseaccess.entities

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class DBAdapter(val context: Context) {
    // Crea conexión a Firestore
    private val dbFirestore = FirebaseFirestore.getInstance()

    // Crea la base de datos
    var dbHelper: DatabaseHelper
    var db: SQLiteDatabase? = null

    // Abre la base de datos
    @Throws(SQLException::class)
    fun open(): DBAdapter {
        db = dbHelper.writableDatabase
        return this
    }

    // Cierra la base de datos
    fun close() {
        dbHelper.close()
    }

    // ------------------------------------------
    // Métodos para la tabla de productos
    // ------------------------------------------

    // Insertar nuevo producto
    fun insertProduct(
        productId: String,
        productName: String,
        supplierId: String,
        categoryId: String,
        quantityPerUnit: String,
        unitPrice: Double,
        unitsInStock: Int,
        unitsOnOrder: Int,
        reorderLevel: Int,
        discontinued: Boolean
    ): Long {
        val initialValues = ContentValues()
        initialValues.put(ProductID, productId)
        initialValues.put(ProductName, productName)
        initialValues.put(SupplierID, supplierId)
        initialValues.put(CategoryID, categoryId)
        initialValues.put(QuantityPerUnit, quantityPerUnit)
        initialValues.put(UnitPrice, unitPrice)
        initialValues.put(UnitsInStock, unitsInStock)
        initialValues.put(UnitsOnOrder, unitsOnOrder)
        initialValues.put(ReorderLevel, reorderLevel)
        initialValues.put(Discontinued, if (discontinued) 1 else 0)
        return db!!.insert(DB_Table, null, initialValues)
    }

    // Método para extraer productos desde Firebase Firestore y guardarlos en SQLite
    fun fetchProductsFromFirestore() {
        dbFirestore.collection("Products")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.w(TAG, "No se encontraron productos en Firestore")
                } else {
                    for (document in documents) {
                        val productId = document.getString("ProductID") ?: continue
                        val productName = document.getString("ProductName") ?: continue
                        val supplierId = document.getString("SupplierID") ?: ""
                        val categoryId = document.getString("CategoryID") ?: ""
                        val quantityPerUnit = document.getString("QuantityPerUnit") ?: ""
                        val unitPrice = document.getDouble("UnitPrice") ?: 0.0
                        val unitsInStock = document.getLong("UnitsInStock")?.toInt() ?: 0
                        val unitsOnOrder = document.getLong("UnitsOnOrder")?.toInt() ?: 0
                        val reorderLevel = document.getLong("ReorderLevel")?.toInt() ?: 0
                        val discontinued = document.getBoolean("Discontinued") ?: false

                        // Insertar cada producto en SQLite
                        insertProduct(
                            productId, productName, supplierId, categoryId,
                            quantityPerUnit, unitPrice, unitsInStock, unitsOnOrder,
                            reorderLevel, discontinued
                        )
                    }
                    Log.d(TAG, "Productos sincronizados desde Firestore a SQLite")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error al recuperar productos de Firestore: ", e)
            }
    }

    // Actualizar un producto
    fun updateProduct(
        id: Long,
        productName: String,
        unitPrice: Double,
        unitsInStock: Int,
        unitsOnOrder: Int,
        reorderLevel: Int,
        discontinued: Boolean
    ): Boolean {
        val args = ContentValues()
        args.put(ProductName, productName)
        args.put(UnitPrice, unitPrice)
        args.put(UnitsInStock, unitsInStock)
        args.put(UnitsOnOrder, unitsOnOrder)
        args.put(ReorderLevel, reorderLevel)
        args.put(Discontinued, if (discontinued) 1 else 0)
        return db!!.update(
            DB_Table,
            args,
            "$ProductID=$id",
            null
        ) > 0
    }

    // Eliminar un producto
    fun deleteProduct(id: Long): Boolean {
        return db!!.delete(
            DB_Table,
            "$ProductID=$id",
            null
        ) > 0
    }

    // Recuperar todos los productos
    val getAllProducts: Cursor
        get() = db!!.query(
            DB_Table,
            arrayOf(
                ProductID,
                ProductName,
                SupplierID,
                CategoryID,
                QuantityPerUnit,
                UnitPrice,
                UnitsInStock,
                UnitsOnOrder,
                ReorderLevel,
                Discontinued
            ),
            null,
            null,
            null,
            null,
            null,
            null
        )

    // Recuperar un producto específico por ID
    fun getProductById(id: String): Cursor? {
        val mCursor = db!!.query(
            true,
            DB_Table,
            arrayOf(
                ProductID,
                ProductName,
                SupplierID,
                CategoryID,
                QuantityPerUnit,
                UnitPrice,
                UnitsInStock,
                UnitsOnOrder,
                ReorderLevel,
                Discontinued
            ),
            "$ProductID=$id",
            null,
            null,
            null,
            null,
            null
        )
        mCursor?.moveToFirst()
        return mCursor
    }

    // Inner class para crear y actualizar la base de datos
    inner class DatabaseHelper(context: Context?) :
        SQLiteOpenHelper(
            context,
            DB_Name,
            null,
            DB_Version
        ) {
        override fun onCreate(db: SQLiteDatabase) {
            try {
                // Crear la tabla de productos
                db.execSQL(createTable)
            } catch (e: SQLException) {
                Log.e(TAG, e.message ?: "")
            }
        }

        override fun onUpgrade(
            db: SQLiteDatabase,
            oldVersion: Int,
            newVersion: Int
        ) {
            Log.w(
                TAG,
                "Actualizando base de datos de la versión $oldVersion a la $newVersion, los datos antiguos serán eliminados"
            )
            db.execSQL("DROP TABLE IF EXISTS productos")
            onCreate(db)
        }
    }

    companion object {
        // Tag para log
        const val TAG = "ProductosDB"

        // Datos generales de la base de datos
        const val DB_Name = "ComprasDB"
        const val DB_Version = 1
        const val DB_Table = "productos"

        // Definición de los campos de la tabla productos
        const val ProductID = "productId"
        const val ProductName = "productName"
        const val SupplierID = "supplierId"
        const val CategoryID = "categoryId"
        const val QuantityPerUnit = "quantityPerUnit"
        const val UnitPrice = "unitPrice"
        const val UnitsInStock = "unitsInStock"
        const val UnitsOnOrder = "unitsOnOrder"
        const val ReorderLevel = "reorderLevel"
        const val Discontinued = "discontinued"

        // Construir el comando SQL para crear la tabla productos
        const val createTable = """
            CREATE TABLE $DB_Table (
                $ProductID TEXT PRIMARY KEY,
                $ProductName TEXT NOT NULL,
                $SupplierID TEXT,
                $CategoryID TEXT,
                $QuantityPerUnit TEXT,
                $UnitPrice REAL,
                $UnitsInStock INTEGER,
                $UnitsOnOrder INTEGER,
                $ReorderLevel INTEGER,
                $Discontinued INTEGER
            );
        """
    }

    // Constructor de la clase
    init {
        dbHelper = DatabaseHelper(context)
    }
}