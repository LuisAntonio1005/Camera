package com.example.camera

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

class AdminSQLiteOpenHelper(context: Context, name: String, factory: CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
//        db.execSQL("CREATE TABLE marcas (id int PRIMARY KEY AUTOINCREMENT,telar TEXT, marca TEXT, planta TEXT  references planta(planta), turno TEXT, fecha TEXT)")
        db.execSQL("CREATE TABLE marcas (telar TEXT, marca TEXT,planta TEXT  references planta(planta), turno TEXT, fecha TEXT)")
        db.execSQL("CREATE TABLE planta (planta TEXT PRIMARY KEY)")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}

